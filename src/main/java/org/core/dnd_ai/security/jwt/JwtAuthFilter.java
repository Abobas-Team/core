package org.core.dnd_ai.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";

    private final Environment environment;

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String auth = environment.matchesProfiles("dev")
                ? request.getHeader(HEADER_NAME)
                : getBearerFromCookies(request.getCookies());

        if (auth == null || !auth.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            var jwt = auth.substring(BEARER_PREFIX.length());
            var username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            }
        } catch (Exception e) {
            log.error(e.getMessage()); // TODO: log correctly
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String getBearerFromCookies(Cookie... cookies) {
        if (cookies == null) return null;

        String tail = null;
        StringBuilder tokenBuilder = new StringBuilder();

        for (var cookie : cookies) {
            if (cookie.getName().equals("BearerHead")) {
                tokenBuilder.append(cookie.getValue());
                if (tail != null) {
                    tokenBuilder.append(tail);
                    break;
                }
            } else if (cookie.getName().equals("BearerTail")) {
                if (tokenBuilder.isEmpty()) {
                    tail = cookie.getValue();
                } else {
                    tokenBuilder.append(cookie.getValue());
                    break;
                }
            }
        }
        return tokenBuilder.isEmpty()
                ? null
                : tokenBuilder.insert(0, BEARER_PREFIX).toString();
    }
}
