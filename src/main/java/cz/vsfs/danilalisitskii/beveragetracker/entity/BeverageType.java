package cz.vsfs.danilalisitskii.beveragetracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "beverage_types")
public class BeverageType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Beverage name cannot be blank.")
    private String name;

    @Column(name = "hydration_factor", nullable = false)
    @NotNull(message = "Hydration factor is required.")
    @DecimalMin(value = "0.1", message = "Hydration factor must be at least 0.1")
    @DecimalMax(value = "1.5", message = "Hydration factor cannot exceed 1.5")
    private Double hydrationFactor;

    @Column(length = 32)
    private String icon;

    public BeverageType(String name, Double hydrationFactor, String icon) {
        this.name = name;
        this.hydrationFactor = hydrationFactor;
        this.icon = icon;
    }
}
