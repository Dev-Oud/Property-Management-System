package property.management.propex.Dto;

import lombok.Builder;
import lombok.Getter;
import property.management.propex.enums.UserType;

@Getter
@Builder
public class OwnerResponse {

    private Long id;
    private String fullName;
    private String email;
    private UserType userType;
}
