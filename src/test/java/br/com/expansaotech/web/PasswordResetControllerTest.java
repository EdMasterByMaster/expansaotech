package br.com.expansaotech.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.expansaotech.domain.PasswordResetToken;
import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import br.com.expansaotech.repository.PasswordResetTokenRepository;
import br.com.expansaotech.repository.UsuarioRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordResetControllerTest {
    private static final String ORIGINAL_PASSWORD = "SenhaAntigaSegura123!";
    private static final String NEW_PASSWORD = "SenhaNovaSegura456!";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void prepararMensagemDeTeste() {
        when(mailSender.createMimeMessage())
                .thenAnswer(invocation -> new MimeMessage(Session.getInstance(new Properties())));
    }

    @Test
    void solicitacaoPublicaNaoRevelaSeEmailExiste() throws Exception {
        Usuario usuario = criarUsuario();
        mockMvc.perform(get("/esqueci-senha")).andExpect(status().isOk());

        mockMvc.perform(post("/esqueci-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "nao-existe@example.org"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/esqueci-senha?solicitado"));

        mockMvc.perform(post("/esqueci-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", usuario.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/esqueci-senha?solicitado"));

        verify(mailSender).send(any(MimeMessage.class));
        mockMvc.perform(get("/esqueci-senha?solicitado"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(
                        "Se o e-mail informado estiver cadastrado")));
    }

    @Test
    void linkAlteraSenhaUmaUnicaVez() throws Exception {
        Usuario usuario = criarUsuario();
        String token = solicitarToken(usuario.getEmail());

        mockMvc.perform(get("/redefinir-senha").param("token", token))
                .andExpect(status().isOk())
                .andExpect(header().string("Referrer-Policy", "no-referrer"))
                .andExpect(content().string(containsString("Nova senha")));

        mockMvc.perform(post("/redefinir-senha")
                        .with(csrf())
                        .param("token", token)
                        .param("senha", NEW_PASSWORD)
                        .param("confirmarSenha", NEW_PASSWORD))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?senhaRedefinida"));

        Usuario atualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(NEW_PASSWORD, atualizado.getSenha()))
                .isTrue();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(ORIGINAL_PASSWORD, atualizado.getSenha()))
                .isFalse();
        org.assertj.core.api.Assertions.assertThat(tokenRepository.findByTokenHash(hashToken(token))).isEmpty();

        mockMvc.perform(post("/redefinir-senha")
                        .with(csrf())
                        .param("token", token)
                        .param("senha", NEW_PASSWORD)
                        .param("confirmarSenha", NEW_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inválido ou expirou")));
    }

    @Test
    void tokenExpiradoNaoPermiteRedefinicao() throws Exception {
        Usuario usuario = criarUsuario();
        String rawToken = "token-expirado-de-teste";
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        tokenRepository.saveAndFlush(new PasswordResetToken(
                usuario, hashToken(rawToken), now.minusMinutes(31), now.minusMinutes(1)));

        mockMvc.perform(get("/redefinir-senha").param("token", rawToken))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inválido ou expirou")));
        mockMvc.perform(post("/redefinir-senha")
                        .with(csrf())
                        .param("token", rawToken)
                        .param("senha", NEW_PASSWORD)
                        .param("confirmarSenha", NEW_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("inválido ou expirou")));

        Usuario semAlteracao = usuarioRepository.findById(usuario.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(
                ORIGINAL_PASSWORD, semAlteracao.getSenha())).isTrue();
    }

    @Test
    void senhasDiferentesMantemTokenValidoEExibeErro() throws Exception {
        Usuario usuario = criarUsuario();
        String token = solicitarToken(usuario.getEmail());

        mockMvc.perform(post("/redefinir-senha")
                        .with(csrf())
                        .param("token", token)
                        .param("senha", NEW_PASSWORD)
                        .param("confirmarSenha", "OutraSenhaSegura789!"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("As senhas não conferem.")));

        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(
                ORIGINAL_PASSWORD, usuario.getSenha())).isTrue();
        org.assertj.core.api.Assertions.assertThat(tokenRepository.findByTokenHash(hashToken(token))).isPresent();
    }

    @Test
    void falhaNoEmailNaoDeixaTokenUtilizavel() throws Exception {
        Usuario usuario = criarUsuario();
        doThrow(new MailSendException("SMTP indisponível"))
                .when(mailSender).send(any(MimeMessage.class));

        mockMvc.perform(post("/esqueci-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", usuario.getEmail()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/esqueci-senha?solicitado"));

        verify(mailSender).send(any(MimeMessage.class));
        org.assertj.core.api.Assertions.assertThat(tokenRepository.existsByUsuario_IdAndCreatedAtAfter(
                usuario.getId(), LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1))).isFalse();
    }

    private Usuario criarUsuario() {
        String digits = UUID.randomUUID().toString().replaceAll("[^0-9]", "");
        String cpf = (digits + "00000000000").substring(0, 11);
        Usuario usuario = new Usuario();
        usuario.setCpf(cpf);
        usuario.setNome("Conta de teste");
        usuario.setEmail("reset-" + UUID.randomUUID() + "@example.org");
        usuario.setSenha(passwordEncoder.encode(ORIGINAL_PASSWORD));
        usuario.setTipo(UsuarioTipo.ADMINISTRACAO);
        return usuarioRepository.saveAndFlush(usuario);
    }

    private String solicitarToken(String email) throws Exception {
        mockMvc.perform(post("/esqueci-senha")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", email))
                .andExpect(status().is3xxRedirection());

        org.mockito.ArgumentCaptor<MimeMessage> messageCaptor =
                org.mockito.ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        String body = messageCaptor.getValue().getContent().toString();
        Matcher matcher = Pattern.compile("token=([A-Za-z0-9_-]+)").matcher(body);
        org.assertj.core.api.Assertions.assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private static String hashToken(String rawToken) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(rawToken.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
