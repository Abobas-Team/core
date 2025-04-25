package org.core.dnd_ai.security.auth;

// TODO: use HTTP-only cookie instead
public record RefresTokenDTO(
    String refreshToken
) {
}
