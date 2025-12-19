package property.management.propex.Repository;


import property.management.propex.Entity.Role;
import property.management.propex.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserType name);
}
