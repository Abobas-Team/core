package org.core.dnd_ai.security.oauth2;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class OAuthUserInfoFactory {
    public OAuthUserInfo getOAuthUserInfo(AuthProvider provider, Map<String, Object> attributes) {
        if (provider.equals(AuthProvider.GOOGLE)) {
            return new GoogleOAuthUserInfo(attributes);
        }
        return null;
    }
}
