package cz.vsfs.danilalisitskii.beveragetracker.service;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.AuthResponseDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.LoginRequestDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.RegisterRequestDto;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Role;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.repository.UserRepository;
import cz.vsfs.danilalisitskii.beveragetracker.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getDailyWaterGoalMl() != null ? request.getDailyWaterGoalMl() : 2000
        );
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponseDto(
                token,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getDailyWaterGoalMl()
        );
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        Role role = user.getRole() != null ? user.getRole() : Role.ROLE_USER;
        String token = jwtService.generateToken(user);

        return new AuthResponseDto(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                role.name(),
                user.getDailyWaterGoalMl() != null ? user.getDailyWaterGoalMl() : 2000
        );
    }
}
