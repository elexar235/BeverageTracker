package cz.vsfs.danilalisitskii.beveragetracker.controller.dto.mappers;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.UserCreateDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.UserDto;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Achievement;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) { return null; }

        var achievementNames = user.getAchievements().stream()
                .map(Achievement::getName)
                .toList();
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDailyWaterGoalMl() != null ? user.getDailyWaterGoalMl() : 2000,
                achievementNames
        );
    }

    public User toEntity(UserCreateDto userCreateDto) {
        return new User(
                userCreateDto.getUsername(),
                userCreateDto.getEmail(),
                userCreateDto.getDailyWaterGoalMl()
        );
    }
}
