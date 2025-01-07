package eoeqs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "import_history")
@Getter
@Setter
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private OAuthUser user;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = true)
    private Integer objectsImported;

    @Column(nullable = true)
    private String errorMessage;
}