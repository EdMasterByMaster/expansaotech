package br.com.expansaotech.service;

import br.com.expansaotech.domain.PasswordResetToken;
import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.repository.PasswordResetTokenRepository;
import br.com.expansaotech.repository.UsuarioRepository;
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
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PasswordResetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetService.class);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRATION_MINUTES = 30;
    private static final int REQUEST_COOLDOWN_MINUTES = 1;

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String publicBaseUrl;
    private final String fromAddress;

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository tokenRepository,
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
    public void solicitarRedefinicao(String email) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        tokenRepository.deleteByExpiresAtBefore(now);

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmailIgnoreCase(email.trim());
        if (usuarioEncontrado.isEmpty()) {
            return;
        }

        Usuario usuario = usuarioEncontrado.get();
        if (tokenRepository.existsByUsuario_IdAndCreatedAtAfter(
                usuario.getId(), now.minusMinutes(REQUEST_COOLDOWN_MINUTES))) {
            return;
        }

        tokenRepository.deleteByUsuario_Id(usuario.getId());
        String rawToken = gerarToken();
        String tokenHash = hashToken(rawToken);
        tokenRepository.save(new PasswordResetToken(
                usuario, tokenHash, now, now.plusMinutes(EXPIRATION_MINUTES)));

        try {
            enviarEmail(usuario.getEmail(), rawToken);
        } catch (MailException | MessagingException exception) {
            tokenRepository.deleteByTokenHash(tokenHash);
            LOGGER.error("Não foi possível enviar redefinição de senha para usuário id={}", usuario.getId(), exception);
        }
    }

    @Transactional(readOnly = true)
    public boolean tokenValido(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return tokenRepository.findByTokenHash(hashToken(rawToken))
                .map(token -> token.getExpiresAt().isAfter(now))
                .orElse(false);
    }

    @Transactional
    public boolean redefinirSenha(String rawToken, String novaSenha) {
        if (rawToken == null
                || rawToken.isBlank()
                || novaSenha == null
                || novaSenha.length() < 12
                || novaSenha.getBytes(StandardCharsets.UTF_8).length > 72) {
            return false;
        }

        Optional<PasswordResetToken> tokenEncontrado =
                tokenRepository.findByTokenHash(hashToken(rawToken));
        if (tokenEncontrado.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenEncontrado.get();
        if (!token.getExpiresAt().isAfter(LocalDateTime.now(ZoneOffset.UTC))) {
            tokenRepository.delete(token);
            return false;
        }

        Usuario usuario = token.getUsuario();
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
        tokenRepository.deleteByUsuario_Id(usuario.getId());
        return true;
    }

    private void enviarEmail(String email, String rawToken) throws MessagingException {
        String baseUrl = publicBaseUrl.replaceAll("/+$", "");
        String link = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/redefinir-senha")
                .queryParam("token", rawToken)
                .build()
                .toUriString();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
        helper.setFrom(fromAddress);
        helper.setTo(email);
        helper.setSubject("Redefinição de senha - Expansão Tech");
        helper.setText("""
                Recebemos uma solicitação para redefinir a senha da sua conta.

                Acesse o link abaixo em até 30 minutos. Ele pode ser usado uma única vez:
                %s

                Se você não solicitou a redefinição, ignore esta mensagem.
                """.formatted(link), false);
        mailSender.send(message);
    }

    private static String gerarToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
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
