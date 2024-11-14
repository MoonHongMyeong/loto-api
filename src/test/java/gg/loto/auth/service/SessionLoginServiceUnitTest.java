package gg.loto.auth.service;

import gg.loto.auth.web.dto.LoginRequest;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionLoginServiceUnitTest {

    @Mock
    private UserService userService;
    
    @Mock
    private HttpSession session;
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    @InjectMocks
    private SessionLoginService loginService;
    
    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {
        @Test
        @DisplayName("로그인 성공")
        void loginSuccess() {
            // given
            String email = "test@example.com";
            String password = "password123";
            String encodedPassword = "encodedPassword123";
            
            LoginRequest loginRequest = new LoginRequest();
            ReflectionTestUtils.setField(loginRequest, "email", email);
            ReflectionTestUtils.setField(loginRequest, "password", password);
            
            User user = User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .nickname("테스터")
                    .build();
                    
            when(userService.findByEmail(email)).thenReturn(user);
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
            
            // when
            loginService.login(loginRequest);
            
            // then
            verify(session).setAttribute(eq("USER"), any(SessionUser.class));
            verify(session).setMaxInactiveInterval(anyInt());
        }
        
        @Test
        @DisplayName("존재하지 않는 이메일로 로그인 시도")
        void loginFailWithInvalidEmail() {
            // given
            String email = "invalid@example.com";
            LoginRequest loginRequest = new LoginRequest();
            ReflectionTestUtils.setField(loginRequest, "email", email);
            
            when(userService.findByEmail(email))
                .thenThrow(new RuntimeException("존재하지 않는 사용자입니다."));
                
            // when & then
            assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
        }
        
        @Test
        @DisplayName("잘못된 비밀번호로 로그인 시도")
        void loginFailWithWrongPassword() {
            // given
            String email = "test@example.com";
            String wrongPassword = "wrongPassword";
            String encodedPassword = "encodedRightPassword";
            
            LoginRequest loginRequest = new LoginRequest();
            ReflectionTestUtils.setField(loginRequest, "email", email);
            ReflectionTestUtils.setField(loginRequest, "password", wrongPassword);
            
            User user = User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .build();
                    
            when(userService.findByEmail(email)).thenReturn(user);
            when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);
            
            // when & then
            assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }
    
    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {
        @Test
        @DisplayName("로그아웃 성공")
        void logoutSuccess() {
            // when
            loginService.logout();
            
            // then
            verify(session).removeAttribute("USER");
            verify(session).invalidate();
        }
    }
} 