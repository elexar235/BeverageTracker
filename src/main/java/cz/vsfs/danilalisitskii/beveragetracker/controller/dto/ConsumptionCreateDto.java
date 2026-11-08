package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConsumptionCreateDto {
    @NotNull(message = "User ID is required")
    private Long userId;

    private Long beverageTypeId;

    @NotNull(message = "Amount is required")
    @Min(value = 10, message = "Amount must be at least 10 ml")
    @Max(value = 3000, message = "Amount cannot exceed 3000 ml per single entry")
    private Integer amountMl;
}
