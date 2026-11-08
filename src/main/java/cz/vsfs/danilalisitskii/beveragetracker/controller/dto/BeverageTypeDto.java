package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeverageTypeDto {
    private Long id;
    private String name;
    private Double hydrationFactor;
    private String icon;
}
