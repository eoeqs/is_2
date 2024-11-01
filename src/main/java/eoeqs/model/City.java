package eoeqs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
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
    @Min(1)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate;

    @Column(nullable = false)
    @Min(1)
    private int area;

    @Column(nullable = false)
    @Min(1)
    private Long population;

//    @PrePersist
    private ZonedDateTime establishmentDate;

    @Column(nullable = false)
    private Boolean capital;

    private int metersAboveSeaLevel;

    @Column(nullable = true)
    @Min(1)
    @Max(1000)
    private Integer carCode;

    @Min(1)
    @Column(nullable = false)
    private long agglomeration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Climate climate;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "governor_id")
    @NotNull(message = "Governor cannot be null")
    private Human governor;
}