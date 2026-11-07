package br.com.expansaotech.config;

import br.com.expansaotech.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) {
        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário ou senha inválidos."));
    }
}
