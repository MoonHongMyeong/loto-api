package gg.loto.user.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import gg.loto.user.domain.User;

@Getter
@NoArgsConstructor
public class UserSaveRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    private String nickname;

    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .build();
    }

    @Builder
    public UserSaveRequest(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
