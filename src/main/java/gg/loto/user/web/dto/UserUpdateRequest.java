package gg.loto.user.web.dto;

import java.util.Optional;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "변경할 닉네임을 입력해주세요.")
    private String nickname;

    @Builder
    public UserUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}