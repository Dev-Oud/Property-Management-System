package property.management.propex.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import property.management.propex.Entity.User;
import property.management.propex.Service.CompanyDocumentService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/company-documents")
@RequiredArgsConstructor
public class CompanyDocumentController {

    private final CompanyDocumentService documentService;

   
@PreAuthorize("hasAnyRole('LANDLORD','AGENT')")
@PostMapping("/{companyId}/upload")
public String uploadDocument(
        @PathVariable Long companyId,
        @RequestParam String documentType,
        @RequestParam MultipartFile file,
        Authentication authentication) throws IOException {

    User user = (User) authentication.getPrincipal();

    documentService.upload(companyId, documentType, file, user);

    return "Document uploaded successfully";
}
}
