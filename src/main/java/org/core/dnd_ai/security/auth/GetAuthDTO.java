package org.core.dnd_ai.security.auth;

public record GetAuthDTO(
    String accessToken,
    String refreshToken
) {
}
