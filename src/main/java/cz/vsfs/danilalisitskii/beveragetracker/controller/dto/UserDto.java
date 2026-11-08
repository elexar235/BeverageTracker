package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private Integer dailyWaterGoalMl;

    private List<String> achievements; // just strings instead of Achievement objects to avoid stackoverflow

    public UserDto(Long id, String username, String email, List<String> achievements) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.dailyWaterGoalMl = 2000;
        this.achievements = achievements;
    }

    public UserDto(Long id, String username, String email, Integer dailyWaterGoalMl, List<String> achievements) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.dailyWaterGoalMl = dailyWaterGoalMl;
        this.achievements = achievements;
    }
}
