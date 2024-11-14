package gg.loto.user.web.dto;

import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String nickname;
    private String password;

    @Builder
    public UserUpdateRequest(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public boolean hasNoChanges() {
        return (nickname == null || nickname.isBlank()) && 
               (password == null || password.isBlank());
    }

    public Optional<String> getNickname() {
        return Optional.ofNullable(nickname).filter(n -> !n.isBlank());
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password).filter(p -> !p.isBlank());
    }
}
