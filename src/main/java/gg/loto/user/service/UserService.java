package gg.loto.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import gg.loto.user.dto.UserResponse;
import gg.loto.user.dto.UserSaveRequest;
import gg.loto.user.entity.User;
import gg.loto.user.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponse createUser(UserSaveRequest userSaveRequest) {
        
        validateDuplicateEmail(userSaveRequest.getEmail());
        userSaveRequest.setEncodedPassword(passwordEncoder.encode(userSaveRequest.getPassword()));
        
        User savedUser = userRepository.save(userSaveRequest.toEntity());
        return UserResponse.of(savedUser);
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) 
            throw new RuntimeException("이미 존재하는 이메일입니다.");
    }
}
