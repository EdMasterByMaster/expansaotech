package br.com.expansaotech.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    private LoginAttemptService service;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptService(usuarioRepository);
        usuario = new Usuario();
        usuario.setEmail("pessoa@exemplo.org");
        when(usuarioRepository.findByEmailIgnoreCase("pessoa@exemplo.org"))
                .thenReturn(Optional.of(usuario));
    }

    @Test
    void bloqueiaAposCincoFalhasEIgnoraNovasTentativasDuranteBloqueio() {
        for (int i = 0; i < 5; i++) {
            service.registrarFalha("pessoa@exemplo.org");
        }

        assertThat(usuario.getTentativasLogin()).isEqualTo(5);
        assertThat(usuario.getBloqueadoAte()).isAfter(LocalDateTime.now());
        assertThat(usuario.isAccountNonLocked()).isFalse();

        service.registrarFalha("pessoa@exemplo.org");
        assertThat(usuario.getTentativasLogin()).isEqualTo(5);
        verify(usuarioRepository, org.mockito.Mockito.times(5)).save(usuario);
    }

    @Test
    void novaFalhaDepoisDaExpiracaoIniciaContagemNovamente() {
        usuario.setTentativasLogin(5);
        usuario.setBloqueadoAte(LocalDateTime.now().minusMinutes(1));

        service.registrarFalha("pessoa@exemplo.org");

        assertThat(usuario.getTentativasLogin()).isEqualTo(1);
        assertThat(usuario.getBloqueadoAte()).isNull();
    }
}
