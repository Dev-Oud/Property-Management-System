package property.management.propex.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import property.management.propex.Entity.Company;
import property.management.propex.Entity.User;
import property.management.propex.Exception.ResourceNotFoundException;
import property.management.propex.Repository.CompanyRepository;
//import property.management.propex.Repository.UserRepository;
import property.management.propex.enums.CompanyStatus;
import property.management.propex.Dto.CompanyRequest;
import property.management.propex.Dto.CompanyResponse;
import property.management.propex.Dto.OwnerResponse;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;


   public CompanyResponse createCompany(CompanyRequest request) {

    User currentUser = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    Company company = Company.builder()
            .companyName(request.getCompanyName())
            .registrationNumber(request.getRegistrationNumber())
            .address(request.getAddress())
            .status(CompanyStatus.PENDING)
            .owner(currentUser)
            .createdAt(LocalDateTime.now())
            .build();

    Company saved = companyRepository.save(company);

    return CompanyResponse.builder()
            .id(saved.getId())
            .companyName(saved.getCompanyName())
            .registrationNumber(saved.getRegistrationNumber())
            .address(saved.getAddress())
            .status(saved.getStatus())
            .createdAt(saved.getCreatedAt())
            .owner(
                OwnerResponse.builder()
                    .id(currentUser.getId())
                    .fullName(currentUser.getFullName())
                    .email(currentUser.getEmail())
                    .userType(currentUser.getUserType())
                    .build()
            )
            .build();
}

    public List<Company> myCompanies(Long ownerId) {
        return companyRepository.findByOwnerId(ownerId);
    }

    public List<Company> pendingCompanies() {
        return companyRepository.findByStatus(CompanyStatus.PENDING.name());
    }

   public Company approveCompany(Long companyId, boolean approve, String reason) {

    Company company = companyRepository.findById(companyId)
          .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

    if (approve) {
        company.setStatus(CompanyStatus.APPROVED);
        company.setRejectionReason(null);
    } else {
        company.setStatus(CompanyStatus.REJECTED);
        company.setRejectionReason(reason);
    }

    return companyRepository.save(company);
}

}
