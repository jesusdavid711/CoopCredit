package com.coopcredit.app.domain.port.in;

import com.coopcredit.app.domain.model.User;

public interface RegisterUserUseCase {
    User execute(User user);
}
