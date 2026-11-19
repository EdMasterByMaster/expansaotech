package br.com.expansaotech.repository;

import br.com.expansaotech.domain.PasswordResetToken;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    boolean existsByUsuario_IdAndCreatedAtAfter(Integer usuarioId, LocalDateTime createdAt);

    void deleteByUsuario_Id(Integer usuarioId);

    void deleteByExpiresAtBefore(LocalDateTime expiresAt);

    void deleteByTokenHash(String tokenHash);
}
