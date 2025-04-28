package org.core.dnd_ai.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtService {
    private final Environment environment;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access_token_lifetime}")
    private Duration accessTokenLifetime;

    private Map<String, Object> formClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        claims.put("roles", roles);
        return claims;
    }

    private Claims extractAllClaims(@NonNull String token) {
        var parser = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .build();
        return parser.parseSignedClaims(token).getPayload();
    }

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(formClaims(userDetails))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenLifetime.toMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();
    }

    public String extractUsername(@NonNull String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<Cookie> cookieForUser(@NonNull UserDetails userDetails) {
        String jwt = generateAccessToken(userDetails);
        Cookie head = getBearerPartCookie("BearerHead", jwt.substring(0, jwt.length() / 2));
        Cookie tail = getBearerPartCookie("BearerTail", jwt.substring(jwt.length() / 2));
        head.setHttpOnly(true);
        return List.of(head, tail);
    }

    private Cookie getBearerPartCookie(String name, String value) {
        var cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) accessTokenLifetime.toMillis() / 1000);

        if (!environment.matchesProfiles("dev")) {
            cookie.setSecure(true);
            cookie.setAttribute("SameSite", "None");
        }
        return cookie;
    }
}
