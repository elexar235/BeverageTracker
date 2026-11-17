package cz.vsfs.danilalisitskii.beveragetracker.controller;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.AuthResponseDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.LoginRequestDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.RegisterRequestDto;
import cz.vsfs.danilalisitskii.beveragetracker.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponseDto register(@RequestBody @Valid RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        return authService.login(request);
    }
}
