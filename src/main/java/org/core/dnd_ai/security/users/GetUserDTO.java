package org.core.dnd_ai.security.users;

import java.time.LocalDateTime;

public record  GetUserDTO(
    String username,
    String email,
    String role,
    String provider,
    LocalDateTime createdAt,
    LocalDateTime lastUpdateAt
) {
}
