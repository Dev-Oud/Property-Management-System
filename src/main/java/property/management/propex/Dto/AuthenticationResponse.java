package property.management.propex.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String message;
    private boolean status;
    private String token;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}
