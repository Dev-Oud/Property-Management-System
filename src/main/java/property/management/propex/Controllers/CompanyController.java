package property.management.propex.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
//import property.management.propex.Dto.CompanyApprovalRequest;
import property.management.propex.Dto.CompanyRequest;
import property.management.propex.Dto.CompanyResponse;
import property.management.propex.Entity.Company;
import property.management.propex.Service.CompanyService;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {



    private final CompanyService companyService;


   @PostMapping
@PreAuthorize("hasAnyRole('LANDLORD','AGENT')")
public CompanyResponse create(@RequestBody CompanyRequest request) {
    return companyService.createCompany(request);
}

  
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public List<Company> pending() {
        return companyService.pendingCompanies();
    }

    
    @PostMapping("/{companyId}/verify")
@PreAuthorize("hasRole('ADMIN')")
public Company verify(
        @PathVariable Long companyId,
        @RequestParam boolean approve,
        @RequestParam(required = false) String reason) {

    return companyService.approveCompany(companyId, approve, reason);
}

}
