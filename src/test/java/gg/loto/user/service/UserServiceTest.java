package gg.loto.user.service;

import gg.loto.user.dto.UserResponse;
import gg.loto.user.dto.UserSaveRequest;
import gg.loto.user.entity.User;
import gg.loto.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp(){
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void createUser_Success(){
        // given
        UserSaveRequest request = UserSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("테스터")
                .build();

        // when
        UserResponse response = userService.createUser(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("테스터");

        // DB 확인
        Optional<User> savedUser = userRepository.findByEmail("test@example.com");
        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시도시 예외 발생")
    void createUser_DuplicateEmail() {
        // given
        UserSaveRequest request = UserSaveRequest.builder()
                .email("test@example.com")
                .password("password123")
                .nickname("테스터1")
                .build();

        userService.createUser(request);

        UserSaveRequest duplicateRequest = UserSaveRequest.builder()
                .email("test@example.com")
                .password("password456")
                .nickname("테스터2")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.createUser(duplicateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }
}
