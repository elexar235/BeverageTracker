package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsumptionDto {
    private Long id;
    private Long userId;
    private String username;
    private Long beverageTypeId;
    private String beverageName;
    private Double hydrationFactor;
    private Integer amountMl;
    private Integer effectiveHydrationMl;
    private LocalDateTime createdAt;

    public ConsumptionDto(Long id, Long userId, String username, Integer amountMl, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.amountMl = amountMl;
        this.effectiveHydrationMl = amountMl;
        this.createdAt = createdAt;
    }
}
