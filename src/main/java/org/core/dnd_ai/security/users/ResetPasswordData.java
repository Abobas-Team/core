package org.core.dnd_ai.security.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordData(
        @NotBlank @Size(min = 8, max = 128) String oldPassword,
        @NotBlank @Size(min = 8, max = 128) String newPassword) {}
