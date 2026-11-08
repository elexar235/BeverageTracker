package cz.vsfs.danilalisitskii.beveragetracker.controller.dto.mappers;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.BeverageTypeCreateDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.BeverageTypeDto;
import cz.vsfs.danilalisitskii.beveragetracker.entity.BeverageType;
import org.springframework.stereotype.Component;

@Component
public class BeverageTypeMapper {

    public BeverageTypeDto toDto(BeverageType beverageType) {
        if (beverageType == null) {
            return null;
        }
        return new BeverageTypeDto(
                beverageType.getId(),
                beverageType.getName(),
                beverageType.getHydrationFactor(),
                beverageType.getIcon()
        );
    }

    public BeverageType toEntity(BeverageTypeCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        return new BeverageType(
                createDto.getName(),
                createDto.getHydrationFactor(),
                createDto.getIcon()
        );
    }
}
