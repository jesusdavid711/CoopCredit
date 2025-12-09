package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.InvalidCredentialsException;
import com.coopcredit.app.domain.model.User;
import com.coopcredit.app.domain.port.in.LoginUseCase;
import com.coopcredit.app.domain.port.out.UserRepositoryPort;
import com.coopcredit.app.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUseCaseImpl(
            UserRepositoryPort userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public String execute(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        return jwtService.generateToken(user.getUsername(), user.getRole().name());
    }
}
