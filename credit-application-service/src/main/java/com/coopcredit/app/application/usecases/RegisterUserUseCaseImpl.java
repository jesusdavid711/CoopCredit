package com.coopcredit.app.application.usecases;

import com.coopcredit.app.domain.exception.BusinessValidationException;
import com.coopcredit.app.domain.model.User;
import com.coopcredit.app.domain.port.in.RegisterUserUseCase;
import com.coopcredit.app.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCaseImpl(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User execute(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessValidationException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);

        return userRepository.save(user);
    }
}
