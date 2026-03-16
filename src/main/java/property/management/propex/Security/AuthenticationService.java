package property.management.propex.Security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import property.management.propex.Dto.AuthenticationRequest;
import property.management.propex.Dto.AuthenticationResponse;
import property.management.propex.Dto.RegisterRequest;
import property.management.propex.Entity.Role;
import property.management.propex.Entity.User;
import property.management.propex.Exception.BadRequestException;
import property.management.propex.Exception.ResourceNotFoundException;
import property.management.propex.Repository.UserRepository;
import property.management.propex.Repository.RoleRepository;
import property.management.propex.enums.UserType;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;


    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Duplicate registration attempt: {}", email);
            throw new BadRequestException("Email already exists");
        }

        UserType userTypeEnum;
        try {
            userTypeEnum = UserType.valueOf(request.getUserType().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid user type provided: {}", request.getUserType());
            throw new BadRequestException("Invalid user type");
        }

     
        Role role = roleRepository.findByName(userTypeEnum)
                .orElseThrow(() -> {
                    log.error("Role not found: {}", userTypeEnum);
                    return new ResourceNotFoundException("Role not found");
                });

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(userTypeEnum)
                .verified(false)
                .walletBalance(0.0)
                .roles(Set.of(role))
                .build();

        userRepository.save(user);

        log.info("User registered successfully: {}", email);

        String jwtToken = jwtService.generateToken(new UserDetailsImpl(user));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

  
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            log.warn("Invalid login attempt for email: {}", email);
            throw new BadCredentialsException("Invalid email or password");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found during login: {}", email);
                    return new ResourceNotFoundException("User not found");
                });

        if (!user.isVerified()) {
            log.warn("Unverified user attempted login: {}", email);
            throw new BadRequestException("Account not verified");
        }

        String jwtToken = jwtService.generateToken(new UserDetailsImpl(user));

        log.info("User logged in successfully: {}", email);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}