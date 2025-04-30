package org.core.dnd_ai.security.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.core.dnd_ai.security.jwt.JwtService;
import org.core.dnd_ai.security.users.User;
import org.core.dnd_ai.security.users.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authManager;

    public GetAuthDTO signUp(@NonNull User user) {
        var saved = userService.save(user);
        return new GetAuthDTO(jwtService.generateAccessToken(saved));
    }

    public GetAuthDTO signIn(@NonNull String username, @NonNull String password) {
        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        var user = (UserDetails) auth.getPrincipal();
        return new GetAuthDTO(jwtService.generateAccessToken(user));
    }

    public void signIn(@NonNull HttpServletResponse response, @NonNull String username, @NonNull String password) {
        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        var user = (UserDetails) auth.getPrincipal();
        var cookies = jwtService.cookieForUser(user);
        cookies.forEach(response::addCookie);
    }
}
