package gg.loto.user.service;

import gg.loto.global.exception.EntityNotFoundException;
import gg.loto.global.exception.ErrorCode;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFindDao {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
    }
}
