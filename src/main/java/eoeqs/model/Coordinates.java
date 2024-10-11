package eoeqs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "coordinates")
@Getter
@Setter
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Min(value = -585, message = "X coordinate must be greater than -586")
    @Column(nullable = false)
    private Float x;

    @Column(nullable = false)
    private double y;

}