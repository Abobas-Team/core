package org.core.dnd_ai.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.core.dnd_ai.security.jwt.JwtService;
import org.core.dnd_ai.security.users.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;

    public String refreshToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            var user = userService.loadUserByUsername(username);
            var storedToken = redisTemplate.opsForValue().get(user.getUsername());
            if (token.equals(storedToken)) return jwtService.generateAccessToken(user);
        } catch (Exception e) {
            log.error(e.getMessage()); // TODO: log correctly
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
