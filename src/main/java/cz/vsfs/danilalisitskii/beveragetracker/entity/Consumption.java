package cz.vsfs.danilalisitskii.beveragetracker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "consumptions")
public class Consumption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "beverage_type_id")
    private BeverageType beverageType;

    private Integer amountMl;

    @Column(name = "effective_hydration_ml")
    private Integer effectiveHydrationMl;

    private LocalDateTime createdAt;

    public Consumption(User user, Integer amountMl, LocalDateTime createdAt) {
        this.user = user;
        this.amountMl = amountMl;
        this.effectiveHydrationMl = amountMl;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Consumption(User user, BeverageType beverageType, Integer amountMl, Integer effectiveHydrationMl, LocalDateTime createdAt) {
        this.user = user;
        this.beverageType = beverageType;
        this.amountMl = amountMl;
        this.effectiveHydrationMl = effectiveHydrationMl;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }
}
