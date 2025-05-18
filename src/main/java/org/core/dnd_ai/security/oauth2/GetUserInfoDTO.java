package org.core.dnd_ai.security.oauth2;

public record GetUserInfoDTO(
    AuthProvider provider,
    String Id,
    String name,
    String email,
    String image
) {
}
