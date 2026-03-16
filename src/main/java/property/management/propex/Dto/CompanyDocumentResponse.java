package property.management.propex.Dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CompanyDocumentResponse {

    private Long id;
    private String documentType;
    private String fileName;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}