package property.management.propex.Dto;

import lombok.Builder;
import lombok.Getter;
import property.management.propex.enums.CompanyStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompanyResponse {

    private Long id;
    private String companyName;
    private String registrationNumber;
    private String address;
    private CompanyStatus status;

    private OwnerResponse owner;
    private LocalDateTime createdAt;
}
