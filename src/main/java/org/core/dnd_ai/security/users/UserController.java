package org.core.dnd_ai.security.users;

import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<GetUserDTO> me(Authentication auth) {
        return ResponseEntity.ok(userMapper.toDTO((User) auth.getPrincipal()));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(Principal principal) {
        userService.deleteByUsername(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(Principal principal, @RequestBody @Validated ResetPasswordData data) {
        userService.resetPassword(data, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
