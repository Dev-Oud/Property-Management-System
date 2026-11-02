package property.management.propex.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import property.management.propex.Entity.User;
import property.management.propex.Service.CompanyDocumentService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import property.management.propex.Dto.CompanyDocumentResponse;

@RestController
@RequestMapping("/api/company-documents")
@RequiredArgsConstructor
public class CompanyDocumentController {

    private final CompanyDocumentService documentService;

    @PreAuthorize("hasAnyRole('LANDLORD','AGENT','ADMIN')")
    @GetMapping("/{companyId}")
    public ResponseEntity<Page<CompanyDocumentResponse>> listDocuments(
            @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "uploadedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(documentService.getCompanyDocuments(companyId, pageable, user));
    }

   
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
