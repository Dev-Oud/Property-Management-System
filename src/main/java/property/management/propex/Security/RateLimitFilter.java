package property.management.propex.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final long REQUESTS_PER_WINDOW = 60;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private static final DefaultRedisScript<Long> INCREMENT_WINDOW = new DefaultRedisScript<>(
            "local n = redis.call('INCR', KEYS[1]); " +
                    "if n == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; " +
                    "return n",
            Long.class);

    private final StringRedisTemplate redisTemplate;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientAddress = request.getHeader("X-Real-IP");
        if (clientAddress == null || clientAddress.isBlank()) {
            clientAddress = request.getRemoteAddr();
        }
        String key = "propex:rate-limit:" + hash(clientAddress.trim());
        try {
            Long count = redisTemplate.execute(INCREMENT_WINDOW, List.of(key),
                    Long.toString(WINDOW.toSeconds()));
            if (count == null) {
                response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "Rate limiter is unavailable");
                return;
            }
            if (count > REQUESTS_PER_WINDOW) {
                response.setHeader("Retry-After", Long.toString(WINDOW.toSeconds()));
                response.sendError(429, "Too many requests. Try again later.");
                return;
            }
            filterChain.doFilter(request, response);
        } catch (DataAccessException ex) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Rate limiter is temporarily unavailable");
        }
    }

    private String hash(String clientAddress) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(clientAddress.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}
