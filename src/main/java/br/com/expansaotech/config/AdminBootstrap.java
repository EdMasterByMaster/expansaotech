package br.com.expansaotech.config;

import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import br.com.expansaotech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private final String cpf;
    private final String name;

    public AdminBootstrap(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_EMAIL:}") String email,
            @Value("${APP_ADMIN_PASSWORD:}") String password,
            @Value("${APP_ADMIN_CPF:}") String cpf,
            @Value("${APP_ADMIN_NAME:Administração Expansão}") String name) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email.trim();
        this.password = password;
        this.cpf = cpf.trim();
        this.name = name.trim();
    }

    @Override
    public void run(ApplicationArguments args) {
        boolean hasEmail = !email.isBlank();
        boolean hasPassword = !password.isBlank();
        boolean hasCpf = !cpf.isBlank();
        if (!hasEmail && !hasPassword && !hasCpf) {
            return;
        }
        if (!hasEmail || !hasPassword || !hasCpf) {
            throw new IllegalStateException(
                    "Configure APP_ADMIN_EMAIL, APP_ADMIN_PASSWORD e APP_ADMIN_CPF juntos.");
        }
        if (password.length() < 12) {
            throw new IllegalStateException("APP_ADMIN_PASSWORD deve ter pelo menos 12 caracteres.");
        }
        if (usuarioRepository.findByEmailIgnoreCase(email).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNome(name);
        admin.setCpf(cpf);
        admin.setEmail(email);
        admin.setSenha(passwordEncoder.encode(password));
        admin.setTipo(UsuarioTipo.ADMINISTRACAO);
        usuarioRepository.save(admin);
    }
}
