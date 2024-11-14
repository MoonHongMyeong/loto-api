package gg.loto.auth.service;

import gg.loto.auth.web.dto.LoginRequest;
import gg.loto.user.web.dto.UserResponse;

public interface LoginService {
    UserResponse login(LoginRequest loginRequest);
    void logout();
}
