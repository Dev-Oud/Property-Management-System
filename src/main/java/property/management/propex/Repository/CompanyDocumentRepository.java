package property.management.propex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property.management.propex.Entity.CompanyDocument;

import java.util.List;

public interface CompanyDocumentRepository
        extends JpaRepository<CompanyDocument, Long> {

    List<CompanyDocument> findByCompanyId(Long companyId);
}
