package br.com.expansaotech.service;

import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAttemptService {
    private static final int MAX_TENTATIVAS = 5;
    private static final int BLOQUEIO_MINUTOS = 15;

    private final UsuarioRepository usuarioRepository;

    public LoginAttemptService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void registrarFalha(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        usuarioRepository.findByEmailIgnoreCase(email.trim()).ifPresent(usuario -> {
            LocalDateTime agora = LocalDateTime.now(ZoneOffset.UTC);
            if (usuario.getBloqueadoAte() != null && !usuario.getBloqueadoAte().isAfter(agora)) {
                usuario.setTentativasLogin(0);
                usuario.setBloqueadoAte(null);
            }
            if (usuario.getBloqueadoAte() == null) {
                int tentativas = usuario.getTentativasLogin() + 1;
                usuario.setTentativasLogin(tentativas);
                if (tentativas >= MAX_TENTATIVAS) {
                    usuario.setBloqueadoAte(agora.plusMinutes(BLOQUEIO_MINUTOS));
                }
                usuarioRepository.save(usuario);
            }
        });
    }

    @Transactional
    public void registrarSucesso(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        usuarioRepository.findByEmailIgnoreCase(email.trim()).ifPresent(usuario -> {
            usuario.setTentativasLogin(0);
            usuario.setBloqueadoAte(null);
            usuarioRepository.save(usuario);
        });
    }
}
