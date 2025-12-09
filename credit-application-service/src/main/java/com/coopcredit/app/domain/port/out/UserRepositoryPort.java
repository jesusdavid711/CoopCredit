package com.coopcredit.app.domain.port.out;

import com.coopcredit.app.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
