package eoeqs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admin")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "admin_roles", joinColumns = @JoinColumn(name = "admin_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    public void setAdminRole() {
        roles.add(Role.ADMIN);
    }
}
