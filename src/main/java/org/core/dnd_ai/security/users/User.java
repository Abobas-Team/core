package org.core.dnd_ai.security.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.core.dnd_ai.security.oauth2.AuthProvider;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DiscriminatorFormula;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "users")
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

    @NotEmpty
    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles = new HashSet<>();

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
        this.provider = provider;

        roles.add(Role.USER);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
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
