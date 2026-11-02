package property.management.propex.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import property.management.propex.Dto.AuthenticationResponse;
import property.management.propex.Entity.AuthSession;
import property.management.propex.Entity.User;
import property.management.propex.Repository.AuthSessionRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private final AuthSessionRepository sessionRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration:2592000000}")
    private long refreshTokenTtlMillis;

    @Transactional
    public AuthenticationResponse issue(User user) {
        UUID accessTokenId = UUID.randomUUID();
        String accessToken = jwtService.generateAccessToken(new UserDetailsImpl(user), accessTokenId);
        String refreshToken = newRefreshToken();

        AuthSession session = new AuthSession();
        session.setAccessTokenId(accessTokenId);
        session.setRefreshTokenHash(hash(refreshToken));
        session.setUser(user);
        session.setCreatedAt(Instant.now());
        session.setRefreshExpiresAt(Instant.now().plusMillis(refreshTokenTtlMillis));
        sessionRepository.save(session);

        return AuthenticationResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenTtlMillis() / 1000)
                .status(true)
                .build();
    }

    @Transactional
    public AuthenticationResponse rotate(String refreshToken) {
        AuthSession current = sessionRepository.findByRefreshTokenHash(hash(refreshToken))
                .filter(session -> session.getRevokedAt() == null)
                .filter(session -> session.getRefreshExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new BadCredentialsException("Refresh token is invalid or expired"));

        if (!current.getUser().isEnabled()) {
            throw new BadCredentialsException("Account is disabled");
        }

        current.setRevokedAt(Instant.now());
        sessionRepository.save(current);
        return issue(current.getUser());
    }

    @Transactional
    public void revokeAccessToken(UUID accessTokenId) {
        sessionRepository.findByAccessTokenIdAndRevokedAtIsNull(accessTokenId)
                .ifPresent(session -> {
                    session.setRevokedAt(Instant.now());
                    sessionRepository.save(session);
                });
    }

    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        sessionRepository.findByRefreshTokenHash(hash(refreshToken))
                .filter(session -> session.getRevokedAt() == null)
                .ifPresent(session -> {
                    session.setRevokedAt(Instant.now());
                    sessionRepository.save(session);
                });
    }

    public boolean isActive(UUID accessTokenId) {
        return sessionRepository.existsByAccessTokenIdAndRevokedAtIsNullAndRefreshExpiresAtAfter(
                accessTokenId, Instant.now());
    }

    @Transactional
    @Scheduled(cron = "0 17 3 * * *", zone = "UTC")
    public void purgeExpiredSessions() {
        sessionRepository.deleteByRefreshExpiresAtBefore(Instant.now());
    }

    private String newRefreshToken() {
        byte[] bytes = new byte[48];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
