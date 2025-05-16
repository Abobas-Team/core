package org.core.dnd_ai.security.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "provider", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = GoogleOAuthUserInfo.class, name = "GOOGLE"),
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract sealed class OAuthUserInfo permits GoogleOAuthUserInfo {
    @NonNull
    protected Map<String, Object> attributes;

    @NonNull
    protected AuthProvider provider;

    @JsonIgnore
    public abstract String getId();

    @JsonIgnore
    public abstract String getName();

    @JsonIgnore
    public abstract String getEmail();

    @JsonIgnore
    public abstract String getImage();
}
