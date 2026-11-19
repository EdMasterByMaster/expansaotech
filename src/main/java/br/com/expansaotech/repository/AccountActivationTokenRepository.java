package br.com.expansaotech.repository;

import br.com.expansaotech.domain.AccountActivationToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountActivationTokenRepository extends JpaRepository<AccountActivationToken, Long> {
    Optional<AccountActivationToken> findByTokenHash(String tokenHash);
    void deleteByUsuario_Id(Integer usuarioId);
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
