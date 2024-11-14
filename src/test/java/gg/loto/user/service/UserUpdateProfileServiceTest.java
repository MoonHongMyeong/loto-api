package gg.loto.user.service;

import gg.loto.user.web.dto.UserResponse;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import gg.loto.user.web.dto.UserUpdateRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserUpdateProfileServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        User user = User.builder()
                .email("test@example.com")
                .password(passwordEncoder.encode("password123"))
                .nickname("테스터")
                .build();
        savedUser = userRepository.save(user);
    }

    @Test
    @DisplayName("사용자 정보 업데이트 성공 - 닉네임만 변경")
    void updateProfileOnlyNicknameSuccess() {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .nickname("새로운닉네임")
                .build();

        // when
        UserResponse response = userService.updateProfile(savedUser.getId(), request);

        // then
        assertThat(response.getNickname()).isEqualTo("새로운닉네임");
        
        User updatedUser = userRepository.findById(savedUser.getId()).get();
        assertThat(updatedUser.getNickname()).isEqualTo("새로운닉네임");
    }

    @Test
    @DisplayName("사용자 정보 업데이트 성공 - 비밀번호만 변경")
    void updateProfileOnlyPasswordSuccess() {
        // given
        String newPassword = "newPassword123";
        UserUpdateRequest request = UserUpdateRequest.builder()
                .password(newPassword)
                .build();

        // when
        UserResponse response = userService.updateProfile(savedUser.getId(), request);

        // then
        User updatedUser = userRepository.findById(response.getId()).get();
        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("사용자 정보 업데이트 성공 - 닉네임과 비밀번호 모두 변경")
    void updateProfileBothFieldsSuccess() {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .nickname("새로운닉네임")
                .password("newPassword123")
                .build();

        // when
        UserResponse response = userService.updateProfile(savedUser.getId(), request);

        // then
        assertThat(response.getNickname()).isEqualTo("새로운닉네임");
        
        User updatedUser = userRepository.findById(savedUser.getId()).get();
        assertThat(updatedUser.getNickname()).isEqualTo("새로운닉네임");
        assertThat(passwordEncoder.matches("newPassword123", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 정보 업데이트 시도")
    void updateProfileUserNotFound() {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .nickname("새로운닉네임")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(999L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }

    @Test
    @DisplayName("빈 업데이트 요청 시도")
    void updateProfileEmptyRequest() {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder().build();

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(savedUser.getId(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("잘못된 수정 요청입니다.");
    }

    @Test
    @DisplayName("공백 문자열로 업데이트 시도")
    void updateProfileBlankStrings() {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .nickname("   ")
                .password("   ")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updateProfile(savedUser.getId(), request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("잘못된 수정 요청입니다.");
    }
}
