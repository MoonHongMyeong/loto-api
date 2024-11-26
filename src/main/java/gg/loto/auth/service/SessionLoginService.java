package gg.loto.auth.service;

import gg.loto.auth.web.dto.LoginRequest;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.user.domain.User;
import gg.loto.user.service.UserFindDao;
import gg.loto.user.web.dto.UserResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService{

    private static final String SESSION_KEY = "USER";
    private static final int SESSION_EXPIRE_TIME = 60 * 60 * 24;

    private final HttpSession session;
    private final UserFindDao userFindDao;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserResponse login(LoginRequest loginRequest) {
        User user = userFindDao.findByEmail(loginRequest.getEmail());

        verifyPassword(loginRequest.getPassword(), user.getPassword());

        createSession(user);

        return UserResponse.of(user);
    }

    private void createSession(User user) {
        session.setAttribute(SESSION_KEY, new SessionUser(user));
        session.setMaxInactiveInterval(SESSION_EXPIRE_TIME);
    }

    @Override
    public void logout() {
        session.removeAttribute(SESSION_KEY);
        session.invalidate();
    }

    private void verifyPassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }
}
