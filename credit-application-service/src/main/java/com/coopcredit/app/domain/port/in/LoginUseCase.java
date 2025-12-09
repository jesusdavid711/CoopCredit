package com.coopcredit.app.domain.port.in;

import com.coopcredit.app.domain.model.LoginResult;

public interface LoginUseCase {
    LoginResult execute(String username, String password);
}
