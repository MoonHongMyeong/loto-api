package gg.loto.user.service;

import gg.loto.auth.service.LoginService;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import gg.loto.user.web.dto.UserResponse;
import gg.loto.user.web.dto.UserSaveRequest;
import gg.loto.user.web.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final LoginService loginService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Transactional
    public User updateProfile(User user, UserUpdateRequest request) {
        user.changeNickname(request.getNickname());
        return user;

    }
    
    @Transactional
    public void withdraw(User user) {
        userRepository.delete(user);
        loginService.logout();
    }

    @Transactional(readOnly = true)
    public User showProfile(User user) {
        return user;
    }
}
