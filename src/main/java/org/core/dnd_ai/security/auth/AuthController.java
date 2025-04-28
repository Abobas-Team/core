package org.core.dnd_ai.security.auth;

import lombok.RequiredArgsConstructor;
import org.core.dnd_ai.global.validation.groups.Post;
import org.core.dnd_ai.security.users.PostUserDTO;
import org.core.dnd_ai.security.users.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserMapper userMapper;
    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<GetAuthDTO> signUp(@RequestBody @Validated(Post.class) PostUserDTO dto) {
        return ResponseEntity.ok(authService.signUp(userMapper.toEntity(dto)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<GetAuthDTO> signIn(@RequestBody PostAuthDTO dto) {
        return ResponseEntity.ok(authService.signIn(dto.email(), dto.password()));
    }
}
