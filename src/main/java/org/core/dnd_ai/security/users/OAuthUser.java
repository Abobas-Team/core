package org.core.dnd_ai.security.users;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.*;
import org.core.dnd_ai.security.oauth2.AuthProvider;
import org.core.dnd_ai.security.oauth2.OAuthUserInfo;
import org.hibernate.annotations.Type;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@DiscriminatorValue("OAUTH")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends User implements OAuth2User {
    @NotNull
    @NonNull
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private OAuthUserInfo userInfo;

    public OAuthUser(@NonNull String email, @NonNull AuthProvider provider, @NonNull OAuthUserInfo userInfo) {
        super(email, email, provider);
        this.userInfo = userInfo;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return userInfo.getAttributes();
    }
}
