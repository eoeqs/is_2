package eoeqs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cities")
@Getter
@Setter
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate;

    @PrePersist
    public void prePersist() {
        creationDate = LocalDate.now();
        if (establishmentDate == null) {
            establishmentDate = ZonedDateTime.now();
        }
    }

    @Column(nullable = false)
    @Min(1)
    private int area;

    @Column(nullable = false)
    @Min(1)
    private Long population;

    private ZonedDateTime establishmentDate;

    @Column(nullable = false)
    private Boolean capital;

    private int metersAboveSeaLevel;


    private Integer carCode;

    private long agglomeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Climate climate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "governor_id")
    @NotNull(message = "Governor cannot be null")
    private Human governor;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = true)
    private LocalDate updatedDate;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDate.now();
    }
}