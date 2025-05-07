package org.core.dnd_ai.security.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import org.core.dnd_ai.security.oauth2.AuthProvider;

@Entity
@DiscriminatorValue("GOOGLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthUser extends User {
    public OAuthUser(@NonNull String email, @NonNull Role role, @NonNull AuthProvider provider) {
        super(email, email, role, provider);
    }
}
