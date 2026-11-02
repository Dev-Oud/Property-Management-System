package property.management.propex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import property.management.propex.Entity.AuthSession;
import jakarta.persistence.LockModeType;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AuthSessionRepository extends JpaRepository<AuthSession, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash);
    Optional<AuthSession> findByAccessTokenIdAndRevokedAtIsNull(UUID accessTokenId);
    boolean existsByAccessTokenIdAndRevokedAtIsNullAndRefreshExpiresAtAfter(
            UUID accessTokenId, Instant now);
    long deleteByRefreshExpiresAtBefore(Instant cutoff);
}
