package property.management.propex.Service;

import property.management.propex.Dto.RegisterRequest;
import property.management.propex.Dto.AuthenticationResponse;
import property.management.propex.Entity.Role;
import property.management.propex.Entity.User;
import property.management.propex.Repository.UserRepository;
import property.management.propex.Repository.RoleRepository;
import property.management.propex.Exception.BadRequestException;
import property.management.propex.Exception.ResourceNotFoundException;
import property.management.propex.enums.UserType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {

        // ✅ Normalize email
        String email = request.getEmail().trim().toLowerCase();

        // ✅ Check duplicate email
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Duplicate registration attempt for email: {}", email);
            throw new BadRequestException("Email already exists");
        }

        // ✅ Convert String → Enum safely
        UserType userTypeEnum;
        try {
            userTypeEnum = UserType.valueOf(request.getUserType().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.warn("Invalid user type provided: {}", request.getUserType());
            throw new BadRequestException("Invalid user type");
        }

        // ✅ Fetch role using enum
        Role role = roleRepository.findByName(userTypeEnum)
                .orElseThrow(() -> {
                    log.error("Role not found: {}", userTypeEnum);
                    return new ResourceNotFoundException("Role not found");
                });

        // ✅ Build user
        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(userTypeEnum) // if your User entity uses enum
                .roles(Set.of(role))
                .verified(false)
                .walletBalance(0.0)
                .build();

        userRepository.save(user);

        log.info("New user registered successfully: {}", email);

        return AuthenticationResponse.builder()
                .message("User registered successfully")
                .status(true)
                .build();
    }
}