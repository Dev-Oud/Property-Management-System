package property.management.propex.Service;

import property.management.propex.Dto.RegisterRequest;
import property.management.propex.Dto.AuthenticationResponse;
import property.management.propex.Entity.Role;
import property.management.propex.Entity.User;
import property.management.propex.Repository.UserRepository;
import property.management.propex.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return AuthenticationResponse.builder()
                    .message("Email already exists")
                    .build();
        }

        Role role = roleRepository.findByName(request.getUserType())
                .orElseThrow(() -> new RuntimeException("Role not found"));

       
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(request.getUserType())  
                .roles(Set.of(role))               
                .verified(false)                   
                .walletBalance(0.0)
                .build();

        userRepository.save(user);

        return AuthenticationResponse.builder()
                .message("User registered successfully")
                .status(true)
                .build();
    }
}
