package property.management.propex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import property.management.propex.Entity.CompanyDocument;

public interface CompanyDocumentRepository
        extends JpaRepository<CompanyDocument, Long> {

    Page<CompanyDocument> findByCompanyId(Long companyId, Pageable pageable);
}
