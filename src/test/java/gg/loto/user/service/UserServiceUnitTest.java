package gg.loto.user.service;

import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import gg.loto.user.web.dto.UserResponse;
import gg.loto.user.web.dto.UserSaveRequest;
import gg.loto.user.web.dto.UserUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    @InjectMocks
    private UserService userService;
    
    @Nested
    @DisplayName("회원가입 테스트")
    class SignUpTest {
        @Test
        @DisplayName("회원가입 성공")
        void signUpSuccess() {
            // given
            UserSaveRequest request = UserSaveRequest.builder()
                    .email("test@example.com")
                    .password("password123")
                    .nickname("테스터")
                    .build();
                    
            User user = request.toEntity(passwordEncoder);
            when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            
            // when
            UserResponse response = userService.signUp(request);
            
            // then
            assertThat(response.getNickname()).isEqualTo(request.getNickname());
            verify(userRepository).save(any(User.class));
        }
        
        @Test
        @DisplayName("중복 이메일 회원가입 실패")
        void signUpDuplicateEmail() {
            // given
            UserSaveRequest request = UserSaveRequest.builder()
                    .email("test@example.com")
                    .build();
                    
            when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(User.builder().build()));
                
            // when & then
            assertThatThrownBy(() -> userService.signUp(request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("이미 존재하는 이메일입니다.");
        }
    }
    
    @Nested
    @DisplayName("프로필 수정 테스트")
    class UpdateProfileTest {
        @Test
        @DisplayName("닉네임 수정 성공")
        void updateNicknameSuccess() {
            // given
            Long userId = 1L;
            User existingUser = User.builder()
                    .nickname("기존닉네임")
                    .build();
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .nickname("새로운닉네임")
                    .build();
                    
            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            
            // when
            UserResponse response = userService.updateProfile(userId, request);
            
            // then
            assertThat(response.getNickname()).isEqualTo("새로운닉네임");
        }

        @Test
        @DisplayName("비밀번호 수정 성공")
        void updatePasswordSuccess() {
            // given
            Long userId = 1L;
            String newPassword = "새로운비밀번호";
            String encodedNewPassword = "인코딩된새로운비밀번호";
            
            User existingUser = User.builder()
                    .password("기존비밀번호")
                    .build();
                    
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .password(newPassword)
                    .build();   

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

            // when
            userService.updateProfile(userId, request);

            // then
            verify(passwordEncoder).encode(newPassword);
        }

        @Test
        @DisplayName("닉네임, 비밀번호 변경 성공")
        void updateProfileBothFieldsSuccess() {
            // given
            Long userId = 1L;
            String newPassword = "새로운비밀번호";
            String encodedNewPassword = "인코딩된새로운비밀번호";
            
            User existingUser = User.builder()
                    .nickname("기존닉네임")
                    .password("기존비밀번호")
                    .build();   
                    
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .nickname("새로운닉네임")
                    .password(newPassword)
                    .build();   

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

            // when
            UserResponse response = userService.updateProfile(userId, request); 

            // then
            assertThat(response.getNickname()).isEqualTo("새로운닉네임");
            verify(passwordEncoder).encode(newPassword);
        }

        @Test
        @DisplayName("프로필 수정 실패 - 존재하지 않는 유저")
        void updateProfileNotFoundUser() {
            // given
            Long userId = 1L;
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .nickname("새로운닉네임")
                    .build();

            // when & then
            assertThatThrownBy(() -> userService.updateProfile(userId, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("존재하지 않는 사용자입니다.");
        }       

        @Test
        @DisplayName("프로필 수정 실패 - 변경 사항이 없음")
        void updateProfileNoChanges() {
            // given
            Long userId = 1L;
            String existingNickname = "기존닉네임";
            User existingUser = User.builder()
                    .nickname(existingNickname)
                    .build();   
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .nickname(existingNickname)
                    .build();   

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

            // when & then
            assertThatThrownBy(() -> userService.updateProfile(userId, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("잘못된 수정 요청입니다.");
        }

        @Test
        @DisplayName("프로필 수정 실패 - 공백 문자열로 업데이트 시도")
        void updateProfileBlankString() {
            // given
            Long userId = 1L;
            UserUpdateRequest request = UserUpdateRequest.builder()
                    .nickname("")
                    .build();       

            // when & then
            assertThatThrownBy(() -> userService.updateProfile(userId, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("잘못된 수정 요청입니다.");
        }   
        
        @Test
        @DisplayName("프로필 수정 실패 - 빈 업데이트 요청")
        void updateProfileEmptyRequest() {
            // given
            Long userId = 1L;
            UserUpdateRequest request = UserUpdateRequest.builder().build();

            // when & then
            assertThatThrownBy(() -> userService.updateProfile(userId, request))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("잘못된 수정 요청입니다.");
        }
    }
} 