package property.management.propex.Entity;

import property.management.propex.enums.UserType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private UserType name;

    public String getSpringRoleName() {
        return "ROLE_" + name.name();
    }
}
