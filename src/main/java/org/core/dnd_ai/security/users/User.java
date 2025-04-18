package org.core.dnd_ai.security.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotNull
    private String name;

    @NonNull
    @NotNull
    private String surname;

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
    private String password;

    @Immutable
    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @Immutable
    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime lastUpdateAt;
}
