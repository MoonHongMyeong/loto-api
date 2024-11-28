package gg.loto.user.web;

import gg.loto.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import gg.loto.user.web.dto.UserUpdateRequest;
import gg.loto.global.auth.LoginUser;
import gg.loto.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<User> updateProfile(@LoginUser User user, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateProfile(user, userUpdateRequest));
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(@LoginUser User user) {
        userService.withdraw(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@LoginUser User user){
        return ResponseEntity.ok(userService.showProfile(user));
    }
}
