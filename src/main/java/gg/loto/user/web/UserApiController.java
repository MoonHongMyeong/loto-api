package gg.loto.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import gg.loto.user.dto.UserSaveRequest;
import gg.loto.user.dto.UserResponse;
import gg.loto.user.dto.UserUpdateRequest;
import gg.loto.user.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserSaveRequest userSaveRequest) {
        return ResponseEntity.ok(userService.createUser(userSaveRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(@PathVariable Long id, @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateProfile(id, userUpdateRequest));
    }
}
