package org.core.dnd_ai.security.auth;

import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.core.dnd_ai.security.jwt.JwtService;
import org.core.dnd_ai.security.users.User;
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

    public GetAuthDTO signUp(@NonNull User user) {
        var saved = userService.save(user);
        String refreshToken = jwtService.generateRefreshToken(saved);
        saveToken(refreshToken);
        return new GetAuthDTO(jwtService.generateAccessToken(saved), refreshToken);
    }

    public GetAuthDTO signIn(@NonNull String username, @NonNull String password) {
        var auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        var user = (UserDetails) auth.getPrincipal();

        String redisKey = REFRESH_TOKEN_PREFIX + user.getUsername();
        String refreshToken = redisTemplate.opsForValue().get(redisKey);

        if (refreshToken == null) {
            refreshToken = jwtService.generateRefreshToken(user);
            saveToken(refreshToken);
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

    private void saveToken(String token) {
        String redisKey = REFRESH_TOKEN_PREFIX + jwtService.extractUsername(token);
        long timeToLive = jwtService.extractExpiration(token).getTime(); // in milliseconds
        redisTemplate.opsForValue().set(redisKey, token, timeToLive, TimeUnit.MILLISECONDS);
    }
}
