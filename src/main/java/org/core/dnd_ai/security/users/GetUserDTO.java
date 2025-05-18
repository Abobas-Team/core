package org.core.dnd_ai.security.users;

import java.time.LocalDateTime;
import java.util.List;
import org.core.dnd_ai.security.oauth2.GetUserInfoDTO;

public record  GetUserDTO(
    String username,
    String email,
    String provider,
    List<String> roles,
    LocalDateTime createdAt,
    LocalDateTime lastUpdateAt,
    GetUserInfoDTO userInfo
) {
}
