package property.management.propex.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import property.management.propex.Dto.AuthenticationRequest;
import property.management.propex.Dto.AuthenticationResponse;
import property.management.propex.Dto.RegisterRequest;
import property.management.propex.Entity.Role;
import property.management.propex.Entity.User;
import property.management.propex.Repository.UserRepository;
import property.management.propex.Repository.RoleRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;

   
    public AuthenticationResponse register(RegisterRequest request) {

        Role role = roleRepository.findByName(request.getUserType())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType())
                .verified(false)
                .walletBalance(0.0)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
        .orElseThrow(() -> new RuntimeException("User not found"));


        var jwtToken = jwtService.generateToken(new UserDetailsImpl(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
