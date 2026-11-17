package br.com.expansaotech.repository;

import br.com.expansaotech.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByCpfIgnoreCase(String cpf);

    boolean existsByEmailIgnoreCase(String email);
}
