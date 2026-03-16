package property.management.propex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property.management.propex.Entity.Company;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByOwnerId(Long ownerId);

    List<Company> findByStatus(String status);
}
