package org.core.dnd_ai.security.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostAuthDTO(
    @NotBlank
    String username,
    @NotBlank
    @Size(min = 8, max = 128)
    String password
) {
}
