package gg.loto.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import gg.loto.user.dto.UserSaveRequest;
import gg.loto.user.dto.UserResponse;
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
}
