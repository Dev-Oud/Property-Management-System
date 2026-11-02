package property.management.propex.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_sessions", indexes = {
        @Index(name = "idx_auth_sessions_user_id", columnList = "user_id"),
        @Index(name = "idx_auth_sessions_expires_at", columnList = "refresh_expires_at")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_auth_sessions_access_jti", columnNames = "access_token_id"),
        @UniqueConstraint(name = "uk_auth_sessions_refresh_hash", columnNames = "refresh_token_hash")
})
@Getter
@Setter
@NoArgsConstructor
public class AuthSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "access_token_id", nullable = false)
    private UUID accessTokenId;

    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_expires_at", nullable = false)
    private Instant refreshExpiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
