package gg.loto.user.dto;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private final String nickname;
    private final String password;

    @Builder
    public UserUpdateRequest(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public Optional<String> getNickname() {
        return Optional.ofNullable(nickname)
                    .filter(n -> !n.trim().isEmpty());
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password)
                    .filter(p -> !p.trim().isEmpty());
    }

    public boolean hasNoChanges() {
        return getNickname().isEmpty() && getPassword().isEmpty();
    }
}
