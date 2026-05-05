package property.management.propex.Controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import property.management.propex.Dto.CompanyRequest;
import property.management.propex.Dto.CompanyResponse;
import property.management.propex.Entity.User;
import property.management.propex.Service.CompanyService;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LANDLORD','AGENT')")
    public ResponseEntity<CompanyResponse> create(
            @Valid @RequestBody CompanyRequest request) {

        CompanyResponse response = companyService.createCompany(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('LANDLORD','AGENT')")
    public ResponseEntity<Page<CompanyResponse>> myCompanies(Pageable pageable) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(
                companyService.myCompanies(user.getId(), pageable)
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CompanyResponse>> pending(Pageable pageable) {

        return ResponseEntity.ok(
                companyService.pendingCompanies(pageable)
        );
    }

    @PostMapping("/{companyId}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> verify(
            @PathVariable Long companyId,
            @RequestParam boolean approve,
            @RequestParam(required = false) String reason) {

        CompanyResponse response =
                companyService.approveCompany(companyId, approve, reason);

        return ResponseEntity.ok(response);
    }
}