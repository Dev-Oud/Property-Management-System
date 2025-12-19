package property.management.propex.Seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import property.management.propex.Entity.Role;
import property.management.propex.enums.UserType;
import property.management.propex.Repository.RoleRepository;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        for (UserType type : UserType.values()) {
            roleRepository.findByName(type)
                .orElseGet(() -> roleRepository.save(new Role(null, type)));
        }
    }
}
