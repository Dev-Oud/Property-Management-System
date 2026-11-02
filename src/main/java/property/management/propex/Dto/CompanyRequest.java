package property.management.propex.Dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class CompanyRequest {
    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String companyName;
    @NotBlank(message = "Registration number is required")
    @Size(max = 80)
    private String registrationNumber;
    @NotBlank(message = "Address is required")
    @Size(max = 255)
    private String address;
}
