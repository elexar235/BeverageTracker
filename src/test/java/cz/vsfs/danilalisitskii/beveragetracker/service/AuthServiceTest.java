package cz.vsfs.danilalisitskii.beveragetracker.service;

import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.AuthResponseDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.LoginRequestDto;
import cz.vsfs.danilalisitskii.beveragetracker.controller.dto.RegisterRequestDto;
import cz.vsfs.danilalisitskii.beveragetracker.entity.Role;
import cz.vsfs.danilalisitskii.beveragetracker.entity.User;
import cz.vsfs.danilalisitskii.beveragetracker.repository.UserRepository;
import cz.vsfs.danilalisitskii.beveragetracker.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldEncodePasswordAndReturnJwtToken() {
        RegisterRequestDto request = new RegisterRequestDto("danila", "danila@vsfs.cz", "secret123", 2200);

        when(userRepository.existsByUsername("danila")).thenReturn(false);
        when(userRepository.existsByEmail("danila@vsfs.cz")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken(any(User.class))).thenReturn("mock.jwt.token");

        AuthResponseDto response = authService.register(request);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("danila", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
        assertEquals(2200, response.getDailyWaterGoalMl());
        verify(passwordEncoder).encode("secret123");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtToken() {
        User user = new User("admin", "admin@beveragetracker.local", 2500);
        user.setId(99L);
        user.setPassword("$2a$10$hashedAdmin");
        user.setRole(Role.ROLE_ADMIN);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("admin123", "$2a$10$hashedAdmin")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("admin.jwt.token");

        AuthResponseDto response = authService.login(new LoginRequestDto("admin", "admin123"));

        assertEquals("admin.jwt.token", response.getToken());
        assertEquals("ROLE_ADMIN", response.getRole());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        User user = new User("danila", "danila@vsfs.cz", 2000);
        user.setPassword("$2a$10$hashedPassword");

        when(userRepository.findByUsername("danila")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> authService.login(new LoginRequestDto("danila", "wrongpass")));
    }
}
