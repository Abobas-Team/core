package org.core.dnd_ai.security.auth;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.core.dnd_ai.security.jwt.JwtService;
import org.core.dnd_ai.security.users.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";

    public GetAuthDTO signIn(@NonNull String username, @NonNull String password) {
        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        var user = (UserDetails) auth.getPrincipal();

        String redisKey = REFRESH_TOKEN_PREFIX + user.getUsername();
        String refreshToken = redisTemplate.opsForValue().get(redisKey);

        if (refreshToken == null) {
            refreshToken = jwtService.generateRefreshToken(user);
            redisTemplate.opsForValue().set(redisKey, refreshToken);
        }
        return new GetAuthDTO(jwtService.generateAccessToken(user), refreshToken);
    }

    public String refreshToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            var user = userService.loadUserByUsername(username);
            var storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + user.getUsername());
            if (token.equals(storedToken)) return jwtService.generateAccessToken(user);
        } catch (Exception e) {
            log.error(e.getMessage()); // TODO: log correctly
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
