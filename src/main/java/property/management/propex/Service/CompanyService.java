package property.management.propex.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import property.management.propex.Entity.Company;
import property.management.propex.Entity.User;
import property.management.propex.Exception.ResourceNotFoundException;
import property.management.propex.Repository.CompanyRepository;
import property.management.propex.enums.CompanyStatus;
import property.management.propex.Dto.CompanyRequest;
import property.management.propex.Dto.CompanyResponse;
import property.management.propex.Dto.OwnerResponse;

import java.time.LocalDateTime;

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

        return mapToResponse(saved);
    }

    public Page<CompanyResponse> myCompanies(Long ownerId, Pageable pageable) {
        return companyRepository.findByOwnerId(ownerId, pageable)
                .map(this::mapToResponse);
    }
    public Page<CompanyResponse> pendingCompanies(Pageable pageable) {
        return companyRepository.findByStatus(CompanyStatus.PENDING, pageable)
                .map(this::mapToResponse);
    }

    public CompanyResponse approveCompany(Long companyId, boolean approve, String reason) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Company not found"));

        if (approve) {
            company.setStatus(CompanyStatus.APPROVED);
            company.setRejectionReason(null);
        } else {
            company.setStatus(CompanyStatus.REJECTED);
            company.setRejectionReason(reason);
        }

        Company updated = companyRepository.save(company);

        return mapToResponse(updated);
    }

    private CompanyResponse mapToResponse(Company company) {

        return CompanyResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .registrationNumber(company.getRegistrationNumber())
                .address(company.getAddress())
                .status(company.getStatus())
                .createdAt(company.getCreatedAt())
                .rejectionReason(company.getRejectionReason())
                .owner(
                        OwnerResponse.builder()
                                .id(company.getOwner().getId())
                                .fullName(company.getOwner().getFullName())
                                .email(company.getOwner().getEmail())
                                .userType(company.getOwner().getUserType())
                                .build()
                )
                .build();
    }
}