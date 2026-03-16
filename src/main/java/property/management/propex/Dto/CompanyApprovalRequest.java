package property.management.propex.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyApprovalRequest {
    private boolean approve;
    private String reason; 
}
