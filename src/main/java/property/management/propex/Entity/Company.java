package property.management.propex.Entity;

import jakarta.persistence.*;
import lombok.*;
import property.management.propex.enums.CompanyStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String registrationNumber; 

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column(length = 500)
    private String rejectionReason;

    


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    

    private LocalDateTime createdAt;
}
