package com.coopcredit.app.domain.port.in;

public interface LoginUseCase {
    String execute(String username, String password);
}
