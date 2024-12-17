package gg.loto.user.service;

import gg.loto.auth.repository.TokenRepository;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import gg.loto.user.web.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Transactional
    public User updateProfile(User user, UserUpdateRequest request) {
        user.changeNickname(request.getNickname());
        return user;

    }
    
    @Transactional
    public void withdraw(User user) {
        tokenRepository.deleteByUser(user);
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public User showProfile(User user) {
        return user;
    }
}
