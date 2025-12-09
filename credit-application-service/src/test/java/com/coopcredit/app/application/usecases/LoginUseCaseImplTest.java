package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.InvalidCredentialsException;
import com.coopcredit.app.domain.model.User;
import com.coopcredit.app.domain.model.enums.Role;
import com.coopcredit.app.domain.port.out.UserRepositoryPort;
import com.coopcredit.app.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginUseCaseImpl loginUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = createUser();
    }

    @Test
    void execute_WithValidCredentials_ReturnsToken() {
        String expectedToken = "jwt.token.here";

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);
        when(jwtService.generateToken("admin", "ROLE_ADMIN")).thenReturn(expectedToken);

        String token = loginUseCase.execute("admin", "password123");

        assertEquals(expectedToken, token);
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("password123", user.getPassword());
        verify(jwtService).generateToken("admin", "ROLE_ADMIN");
    }

    @Test
    void execute_WithInvalidUsername_ThrowsException() {
        when(userRepository.findByUsername("wronguser")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> {
            loginUseCase.execute("wronguser", "password123");
        });

        verify(userRepository).findByUsername("wronguser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void execute_WithInvalidPassword_ThrowsException() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> {
            loginUseCase.execute("admin", "wrongpassword");
        });

        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("wrongpassword", user.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    @Test
    void execute_WithInactiveUser_ThrowsException() {
        user.setActive(false);

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", user.getPassword())).thenReturn(true);

        assertThrows(InvalidCredentialsException.class, () -> {
            loginUseCase.execute("admin", "password123");
        });

        verify(jwtService, never()).generateToken(anyString(), anyString());
    }

    private User createUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("admin");
        u.setPassword("$2a$10$encodedPassword");
        u.setRole(Role.ROLE_ADMIN);
        u.setActive(true);
        return u;
    }
}
