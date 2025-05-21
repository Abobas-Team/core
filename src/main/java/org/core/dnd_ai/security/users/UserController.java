package org.core.dnd_ai.security.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<GetUserDTO> me(Authentication auth) {
        return ResponseEntity.ok(userMapper.toDTO((User) auth.getPrincipal()));
    }
}
