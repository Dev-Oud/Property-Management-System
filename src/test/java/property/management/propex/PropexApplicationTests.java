package property.management.propex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
@ActiveProfiles("test")
class PropexApplicationTests {

	private static final String TEST_JWT_SECRET = randomTestSecret();

	@DynamicPropertySource
	static void testProperties(DynamicPropertyRegistry registry) {
		registry.add("jwt.secret", () -> TEST_JWT_SECRET);
	}

	private static String randomTestSecret() {
		byte[] key = new byte[32];
		new SecureRandom().nextBytes(key);
		return Base64.getEncoder().encodeToString(key);
	}

	@Test
	void contextLoads() {
	}

}
