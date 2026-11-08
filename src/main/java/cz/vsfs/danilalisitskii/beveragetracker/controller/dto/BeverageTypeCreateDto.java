package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeverageTypeCreateDto {

    @NotBlank(message = "Beverage name cannot be blank")
    private String name;

    @NotNull(message = "Hydration factor is required")
    @DecimalMin(value = "0.1", message = "Hydration factor must be at least 0.1")
    @DecimalMax(value = "1.5", message = "Hydration factor cannot exceed 1.5")
    private Double hydrationFactor;

    private String icon;
}
