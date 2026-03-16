package property.management.propex.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import property.management.propex.Dto.CompanyDocumentResponse;
import property.management.propex.Entity.Company;
import property.management.propex.Entity.CompanyDocument;
import property.management.propex.Entity.User;
import property.management.propex.Exception.BadRequestException;
import property.management.propex.Exception.ForbiddenException;
import property.management.propex.Exception.ResourceNotFoundException;
import property.management.propex.Repository.CompanyDocumentRepository;
import property.management.propex.Repository.CompanyRepository;
import property.management.propex.enums.CompanyStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyDocumentService {

    private final CompanyRepository companyRepository;
    private final CompanyDocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void upload(
            Long companyId,
            String documentType,
            MultipartFile file,
            User user) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File cannot be empty");
        }

        
        String contentType = file.getContentType();

        if (contentType == null ||
                (!contentType.equals("application/pdf") &&
                 !contentType.equals("image/jpeg") &&
                 !contentType.equals("image/png"))) {

            throw new BadRequestException("Only PDF, JPG, and PNG files are allowed");
        }

        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new BadRequestException("File size exceeds 5MB limit");
        }

       
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found"));

        
        if (!company.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not allowed to upload for this company");
        }

        try {
            
            Path companyUploadPath = Paths.get(uploadDir, companyId.toString());
            Files.createDirectories(companyUploadPath);

        
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = companyUploadPath.resolve(fileName);

            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

          
            company.setStatus(CompanyStatus.UNDER_REVIEW);

            CompanyDocument document = CompanyDocument.builder()
                    .company(company)
                    .documentType(documentType)
                    .filePath(filePath.toString())
                    .uploadedBy(user)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            documentRepository.save(document);
            companyRepository.save(company);

        } catch (IOException e) {
            throw new BadRequestException("Failed to store file");
        }
    }

    public List<CompanyDocumentResponse> getCompanyDocuments(Long companyId) {

        if (!companyRepository.existsById(companyId)) {
            throw new ResourceNotFoundException("Company not found");
        }

        List<CompanyDocument> documents =
                documentRepository.findByCompanyId(companyId);

        return documents.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CompanyDocument getDocumentById(Long documentId) {

        return documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Document not found"));
    }

    private CompanyDocumentResponse mapToResponse(CompanyDocument doc) {
        return CompanyDocumentResponse.builder()
                .id(doc.getId())
                .documentType(doc.getDocumentType())
                .fileName(Paths.get(doc.getFilePath())
                        .getFileName()
                        .toString())
                .uploadedBy(doc.getUploadedBy().getFullName())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }
}