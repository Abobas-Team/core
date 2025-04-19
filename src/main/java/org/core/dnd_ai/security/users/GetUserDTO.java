package org.core.dnd_ai.security.users;

import java.time.LocalDateTime;

public record GetUserDTO(
    String name,
    String surname,
    String email,
    String role,
    LocalDateTime createdAt,
    LocalDateTime lastUpdateAt
) {
}
