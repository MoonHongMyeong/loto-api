package gg.loto.user.service;

import gg.loto.auth.service.LoginService;
import gg.loto.global.auth.dto.SessionUser;
import gg.loto.user.web.dto.UserUpdateRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import gg.loto.user.web.dto.UserResponse;
import gg.loto.user.web.dto.UserSaveRequest;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final LoginService loginService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Transactional
    public UserResponse signUp(UserSaveRequest userSaveRequest) {
        
        validateDuplicateEmail(userSaveRequest.getEmail());
        userSaveRequest.setEncodedPassword(passwordEncoder.encode(userSaveRequest.getPassword()));
        
        User savedUser = userRepository.save(userSaveRequest.toEntity());
        return UserResponse.of(savedUser);
    }

    @Transactional(readOnly = true)
    public void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) 
            throw new RuntimeException("이미 존재하는 이메일입니다.");
    }

    @Transactional
    public UserResponse updateProfile(Long id, UserUpdateRequest request) {
        if (request.hasNoChanges()) {
            throw new RuntimeException("잘못된 수정 요청입니다.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        boolean hasChanges = false;

        if (request.getNickname().isPresent()) {
            String newNickname = request.getNickname().get();
            if (!newNickname.equals(user.getNickname())) {
                user.changeNickname(newNickname);
                hasChanges = true;
            }
        }

        if (request.getPassword().isPresent()) {
            user.changePassword(passwordEncoder.encode(request.getPassword().get()));
            hasChanges = true;
        }

        if (!hasChanges) {
            throw new RuntimeException("잘못된 수정 요청입니다.");
        }

        return UserResponse.of(user);
    }
    
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
    }

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
        
        userRepository.delete(user);
        
        loginService.logout();
    }

    @Transactional(readOnly = true)
    public UserResponse showProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
        return UserResponse.of(user);
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(SessionUser sessionUser) {
        return userRepository.findById(sessionUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
    }
}
