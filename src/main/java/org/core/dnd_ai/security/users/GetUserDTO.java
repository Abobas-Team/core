package org.core.dnd_ai.security.users;

import java.time.LocalDateTime;
import java.util.List;

public record  GetUserDTO(
    String username,
    String email,
    String provider,
    List<String> roles,
    LocalDateTime createdAt,
    LocalDateTime lastUpdateAt
) {
}
