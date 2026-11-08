package cz.vsfs.danilalisitskii.beveragetracker.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Doesn't look like an email, lol")
    private String email;

    @Min(value = 500, message = "Daily water goal must be at least 500 ml")
    @Max(value = 10000, message = "Daily water goal cannot exceed 10000 ml")
    private Integer dailyWaterGoalMl;

    public UserCreateDto(String username, String email) {
        this.username = username;
        this.email = email;
        this.dailyWaterGoalMl = 2000;
    }
}
