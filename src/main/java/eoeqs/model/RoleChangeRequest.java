package eoeqs.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_change_requests")
@Data
public class RoleChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_role")
    private Role requestedRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
