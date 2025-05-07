package org.core.dnd_ai.security.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.core.dnd_ai.security.oauth2.AuthProvider;

@Entity
@DiscriminatorValue("LOCAL")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalUser extends User {
    @NonNull
    @NotNull
    private String password;

    public LocalUser(@NonNull String username, @NonNull String email, @NonNull String password) {
        super(username, email, AuthProvider.LOCAL);
        this.password = password;
    }

    @Override
    public @NonNull String getPassword() {
        return password;
    }
}
