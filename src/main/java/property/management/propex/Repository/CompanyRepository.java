package property.management.propex.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import property.management.propex.Entity.Company;
import property.management.propex.enums.CompanyStatus;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);
    Page<Company> findByOwnerIdAndStatus(Long ownerId, CompanyStatus status, Pageable pageable);
}