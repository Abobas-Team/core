package org.core.dnd_ai.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

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

    private boolean isTokenExpired(@NonNull String token) {
        var expirationDate = extractAllClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(formClaims(userDetails))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifetime.toMillis()))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret)))
                .compact();
    }

    public String extractUsername(@NonNull String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        var username = userDetails.getUsername();
        return !isTokenExpired(token) && username.equals(extractUsername(token));
    }
}
