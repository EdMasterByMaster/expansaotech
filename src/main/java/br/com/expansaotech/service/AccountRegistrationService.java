package br.com.expansaotech.service;

import br.com.expansaotech.domain.AccountActivationToken;
import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import br.com.expansaotech.repository.AccountActivationTokenRepository;
import br.com.expansaotech.repository.UsuarioRepository;
import br.com.expansaotech.web.dto.PerfilInput;
import br.com.expansaotech.web.dto.RegistrationInput;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AccountRegistrationService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRATION_HOURS = 24;

    private final UsuarioRepository usuarioRepository;
    private final AccountActivationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String publicBaseUrl;
    private final String fromAddress;

    public AccountRegistrationService(
            UsuarioRepository usuarioRepository,
            AccountActivationTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${app.password-reset.public-base-url}") String publicBaseUrl,
            @Value("${app.password-reset.from}") String fromAddress) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.publicBaseUrl = publicBaseUrl;
        this.fromAddress = fromAddress;
    }

    @Transactional
    public void cadastrar(RegistrationInput input) {
        String email = input.email().trim();
        if (!input.senha().equals(input.confirmarSenha())) {
            throw new IllegalArgumentException("As senhas informadas não coincidem.");
        }
        if (input.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("A senha deve ter no máximo 72 bytes.");
        }
        if (input.tipo() != UsuarioTipo.ALUNO && input.tipo() != UsuarioTipo.VOLUNTARIO
                && input.tipo() != UsuarioTipo.DOADOR) {
            throw new IllegalArgumentException("Selecione um tipo de conta permitido.");
        }
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Já existe uma conta cadastrada com este e-mail.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(input.nome());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(input.senha()));
        usuario.setTipo(input.tipo());
        usuario.setAtivo(false);
        usuario.setEmailVerificado(false);
        usuario.setPerfilCompleto(false);
        usuarioRepository.save(usuario);

        tokenRepository.deleteByUsuario_Id(usuario.getId());
        String rawToken = gerarToken();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        tokenRepository.save(new AccountActivationToken(
                usuario, hashToken(rawToken), now, now.plusHours(EXPIRATION_HOURS)));
        enviarEmail(usuario.getEmail(), rawToken);
    }

    @Transactional
    public boolean ativar(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }

        return tokenRepository.findByTokenHash(hashToken(rawToken))
                .map(token -> {
                    if (!token.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
                        tokenRepository.delete(token);
                        return false;
                    }
                    Usuario usuario = token.getUsuario();
                    usuario.setEmailVerificado(true);
                    usuario.setAtivo(true);
                    usuarioRepository.save(usuario);
                    tokenRepository.delete(token);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public void convidarColaborador(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado."));
        if (usuario.getTipo() != UsuarioTipo.COLABORADOR) {
            throw new IllegalArgumentException("O convite de ativação está disponível para colaboradores.");
        }
        usuario.setAtivo(false);
        usuario.setEmailVerificado(false);
        usuario.setPerfilCompleto(false);
        usuarioRepository.save(usuario);
        tokenRepository.deleteByUsuario_Id(usuarioId);
        String rawToken = gerarToken();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        tokenRepository.save(new AccountActivationToken(
                usuario, hashToken(rawToken), now, now.plusHours(EXPIRATION_HOURS)));
        enviarEmail(usuario.getEmail(), rawToken);
    }

    @Transactional
    public void completarPerfil(Integer usuarioId, PerfilInput input) {
        String cpf = input.cpf().replaceAll("\\D", "");
        if (!cpfValido(cpf)) {
            throw new IllegalArgumentException("Informe um CPF válido.");
        }
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado."));
        if (usuario.getTipo() == UsuarioTipo.VOLUNTARIO
                && (input.diasDisponiveis() == null || input.diasDisponiveis().isEmpty())) {
            throw new IllegalArgumentException("Selecione ao menos um dia disponível para voluntariado.");
        }
        if (usuarioRepository.existsByCpf(cpf)
                && !cpf.equals(usuario.getCpf())) {
            throw new IllegalArgumentException("Este CPF já está cadastrado.");
        }
        usuario.setCpf(cpf);
        usuario.setCep(input.cep().replaceAll("\\D", ""));
        usuario.setLogradouro(input.logradouro());
        usuario.setNumero(input.numero());
        usuario.setComplemento(input.complemento());
        usuario.setDataNascimento(input.dataNascimento());
        usuario.setTelefone(input.telefone());
        usuario.setAreasInteresse(input.areasInteresse());
        usuario.setDiasDisponiveis(input.diasDisponiveis());
        usuario.setPerfilCompleto(true);
        usuarioRepository.save(usuario);
    }

    private void enviarEmail(String email, String rawToken) {
        String link = UriComponentsBuilder.fromUriString(publicBaseUrl.replaceAll("/+$", ""))
                .path("/ativar-conta")
                .queryParam("token", rawToken)
                .build()
                .toUriString();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setFrom(fromAddress);
            helper.setTo(email);
            helper.setSubject("Ative sua conta - Expansão Tech");
            helper.setText("""
                    Para ativar sua conta, acesse o link abaixo em até 24 horas:
                    %s
                    """.formatted(link), false);
            mailSender.send(message);
        } catch (MailException | MessagingException exception) {
            throw new IllegalStateException("Não foi possível enviar o e-mail de ativação.", exception);
        }
    }

    private static boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        int primeiro = digitoCpf(cpf, 9);
        int segundo = digitoCpf(cpf, 10);
        return cpf.charAt(9) - '0' == primeiro && cpf.charAt(10) - '0' == segundo;
    }

    private static int digitoCpf(String cpf, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (cpf.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static String gerarToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String hashToken(String rawToken) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 não está disponível.", exception);
        }
    }
}
