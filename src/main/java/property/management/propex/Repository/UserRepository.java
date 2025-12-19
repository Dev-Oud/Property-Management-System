package property.management.propex.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import property.management.propex.Entity.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailIgnoreCase(String email);
}