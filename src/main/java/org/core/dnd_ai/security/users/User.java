package org.core.dnd_ai.security.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.core.dnd_ai.security.oauth2.AuthProvider;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "base_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula(
        value =
                """
            CASE WHEN provider = 'LOCAL' THEN 'LOCAL'
            WHEN provider = 'GOOGLE' THEN 'OAUTH'
            ELSE 'UNKNOWN' END
        """,
        discriminatorType = DiscriminatorType.STRING)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotNull
    @Column(unique = true)
    private String username;

    @NonNull
    @NotNull
    @Column(unique = true)
    private String email;

    @NonNull
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @NonNull
    @NotNull
    @Setter(AccessLevel.NONE)
    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Immutable
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Immutable
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastUpdateAt;

    protected User(@NonNull String username, @NonNull String email, @NonNull AuthProvider provider) {
        this.username = username;
        this.email = email;
        this.role = Role.USER;
        this.provider = provider;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public @NonNull String getUsername() {
        return username;
    }
}
