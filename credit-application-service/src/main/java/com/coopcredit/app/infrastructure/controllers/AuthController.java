package com.coopcredit.app.infrastructure.controllers;

import com.coopcredit.app.domain.model.User;
import com.coopcredit.app.domain.port.in.LoginUseCase;
import com.coopcredit.app.domain.port.in.RegisterUserUseCase;
import com.coopcredit.app.infrastructure.web.dto.AuthResponse;
import com.coopcredit.app.infrastructure.web.dto.LoginRequest;
import com.coopcredit.app.infrastructure.web.dto.RegisterUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase, LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        User registeredUser = registerUserUseCase.execute(user);

        String token = loginUseCase.execute(registeredUser.getUsername(), request.getPassword());

        return ResponseEntity
                .ok(new AuthResponse(token, registeredUser.getUsername(), registeredUser.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        String token = loginUseCase.execute(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(new AuthResponse(token, request.getUsername(), null));
    }
}
