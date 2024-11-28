package gg.loto.auth.service;

import gg.loto.auth.web.dto.DiscordTokenResponse;
import gg.loto.user.web.dto.UserResponse;

public interface LoginService {
    UserResponse login(DiscordTokenResponse token);
    void logout();
}
