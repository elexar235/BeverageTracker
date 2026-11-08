package cz.vsfs.danilalisitskii.beveragetracker.controller.dto.mappers;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.ConsumptionDto;
import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Consumption;
import org.springframework.stereotype.Component;

@Component
public class ConsumptionMapper {

    public ConsumptionDto toDto(Consumption consumption) {
        if (consumption == null) return null;

        BeverageType bt = consumption.getBeverageType();
        Long beverageTypeId = bt != null ? bt.getId() : null;
        String beverageName = bt != null ? bt.getName() : "Water";
        Double hydrationFactor = bt != null ? bt.getHydrationFactor() : 1.0;
        Integer effectiveHydrationMl = consumption.getEffectiveHydrationMl() != null
                ? consumption.getEffectiveHydrationMl()
                : consumption.getAmountMl();

        return new ConsumptionDto(
                consumption.getId(),
                consumption.getUser().getId(),
                consumption.getUser().getUsername(),
                beverageTypeId,
                beverageName,
                hydrationFactor,
                consumption.getAmountMl(),
                effectiveHydrationMl,
                consumption.getCreatedAt()
        );
    }
}
