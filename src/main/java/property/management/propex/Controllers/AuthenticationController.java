package property.management.propex.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import property.management.propex.Dto.*;
import property.management.propex.Security.AuthenticationService;
import property.management.propex.Security.AuthSessionService;
import property.management.propex.Security.JwtService;
import property.management.propex.Dto.RefreshRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthSessionService authSessionService;
    private final JwtService jwtService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration request received");

        AuthenticationResponse response =
                authenticationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "User registered successfully", response));
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {

        log.info("Login attempt received");

        AuthenticationResponse response =
                authenticationService.authenticate(request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", response)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refresh(
            @Valid @RequestBody RefreshRequest request) {
        AuthenticationResponse response = authSessionService.rotate(request.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(true, "Tokens refreshed", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestBody(required = false) RefreshRequest request) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                authSessionService.revokeAccessToken(jwtService.extractTokenId(authorization.substring(7).trim()));
                return ResponseEntity.ok(new ApiResponse<>(true, "Session revoked", null));
            } catch (RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid access token", null));
            }
        }
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "A bearer access token or refresh token is required", null));
        }
        authSessionService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(true, "Session revoked", null));
    }
}
