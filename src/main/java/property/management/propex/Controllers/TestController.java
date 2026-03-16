package property.management.propex.Controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin() {
        return "ADMIN ACCESS OK";
    }

    @PreAuthorize("hasRole('LANDLORD')")
    @GetMapping("/landlord")
    public String landlord() {
        return "LANDLORD ACCESS OK";
    }

    @PreAuthorize("hasAnyRole('LANDLORD','AGENT')")
    @GetMapping("/manage")
    public String manage() {
        return "LANDLORD or AGENT ACCESS OK";
    }

    @GetMapping("/whoami")
    public Object whoAmI(Authentication authentication) {
        return authentication.getAuthorities();
    }
}
