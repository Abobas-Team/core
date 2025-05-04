package org.core.dnd_ai.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.core.dnd_ai.global.validation.groups.Post;
import org.core.dnd_ai.security.users.GetUserDTO;
import org.core.dnd_ai.security.users.PostUserDTO;
import org.core.dnd_ai.security.users.UserMapper;
import org.springframework.core.env.Environment;
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
    private final Environment environment;

    @PostMapping("/sign-up")
    public ResponseEntity<GetUserDTO> signUp(
            HttpServletResponse response, @RequestBody @Validated(Post.class) PostUserDTO dto) {
        var user = userMapper.toEntity(dto);
        return ResponseEntity.ok(userMapper.toDTO(authService.signUp(response, user)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(HttpServletResponse response, @RequestBody PostAuthDTO dto) {
        var responseEntityBuilder = ResponseEntity.ok();
        if (environment.matchesProfiles("dev")) {
            return responseEntityBuilder.body(authService.signIn(dto.username(), dto.password()));
        }
        authService.signIn(response, dto.username(), dto.password());
        return responseEntityBuilder.build();
    }
}
