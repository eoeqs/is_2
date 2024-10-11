package eoeqs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "humans")
@Getter
@Setter
public class Human {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Height cannot be null")
    @Min(value = 1)
    @Column(nullable = false)
    private Double height;
}