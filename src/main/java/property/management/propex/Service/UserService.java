package property.management.propex.Service;


import property.management.propex.Dto.RegisterRequest;
import property.management.propex.Dto.AuthenticationResponse;

public interface UserService {
    AuthenticationResponse register(RegisterRequest request);
}
