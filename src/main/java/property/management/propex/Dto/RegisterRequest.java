package property.management.propex.Dto;


import property.management.propex.enums.UserType;
import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    private String email;
    private String password;
    private UserType userType; 
}

