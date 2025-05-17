package org.core.dnd_ai.security.oauth2;

import lombok.RequiredArgsConstructor;
import org.core.dnd_ai.security.users.OAuthUser;
import org.core.dnd_ai.security.users.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserService userService;
    private final OAuthUserInfoFactory oAuthUserInfoFactory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(e.getMessage(), e.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        if (!userService.existsByEmail(email)) {
            var provider = this.getAuthProvider(oAuth2UserRequest);
            var userInfo = oAuthUserInfoFactory.getOAuthUserInfo(provider, oAuth2User.getAttributes());
            userService.save(new OAuthUser(email, provider, userInfo));
        }
        return oAuth2User;
    }

    private AuthProvider getAuthProvider(OAuth2UserRequest oAuth2UserRequest) {
        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        return switch (provider) {
            case "google" -> AuthProvider.GOOGLE;
            case null -> throw new OAuth2AuthenticationException("Unsupported provider");
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + provider);
        };
    }
}
