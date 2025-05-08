package org.core.dnd_ai.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.core.dnd_ai.security.jwt.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException {
        if (response.isCommitted()) {
            return;
        }

        var oAuth2User = (OAuth2User) auth.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        assert email != null : "Email should never be null here";

        var cookies = jwtService.cookieForUser(email);
        cookies.forEach(response::addCookie);

        // TODO: change redirect to uri in request
        getRedirectStrategy().sendRedirect(request, response, "https://www.youtube.com/watch?v=3HrSVXP99kQ");
    }
}
