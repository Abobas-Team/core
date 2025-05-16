package org.core.dnd_ai.security.oauth2;

import java.util.Map;
import lombok.NonNull;

public final class GoogleOAuthUserInfo extends OAuthUserInfo {
    public GoogleOAuthUserInfo(@NonNull Map<String, Object> attributes) {
        super(attributes, AuthProvider.GOOGLE);
    }

    @Override
    public String getId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getImage() {
        return attributes.get("picture").toString();
    }
}
