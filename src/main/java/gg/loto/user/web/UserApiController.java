package gg.loto.user.web;

import gg.loto.global.auth.dto.SessionUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import gg.loto.user.web.dto.UserSaveRequest;
import gg.loto.user.web.dto.UserResponse;
import gg.loto.user.web.dto.UserUpdateRequest;
import gg.loto.global.auth.LoginUser;
import gg.loto.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> signUp(@RequestBody UserSaveRequest userSaveRequest) {
        return ResponseEntity.ok(userService.signUp(userSaveRequest));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(@LoginUser SessionUser sessionUser, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateProfile(sessionUser.getId(), userUpdateRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@LoginUser SessionUser sessionUser) {
        userService.withdraw(sessionUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@LoginUser SessionUser sessionUser){
        return ResponseEntity.ok(userService.showProfile(sessionUser.getId()));
    }
}
