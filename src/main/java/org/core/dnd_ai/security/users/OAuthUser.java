package org.core.dnd_ai.security.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.util.Map;
import lombok.*;
import org.core.dnd_ai.security.oauth2.AuthProvider;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@DiscriminatorValue("OAUTH")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends User implements OAuth2User {
    public OAuthUser(@NonNull String email, @NonNull AuthProvider provider) {
        super(email, email, provider);
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    @Override
    @Transient
    public Map<String, Object> getAttributes() {
        return Map.of();
    }
}
