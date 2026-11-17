package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private Long userId;
    private String username;
    private String email;
    private String role;
    private Integer dailyWaterGoalMl;
}
