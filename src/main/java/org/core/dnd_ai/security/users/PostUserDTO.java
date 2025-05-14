package org.core.dnd_ai.security.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;
import org.core.dnd_ai.global.validation.groups.Post;

public record PostUserDTO(
    @NotBlank(groups = Post.class)
    @Size(min = 5, max = 255, groups = {Default.class, Post.class})
    String username,

    @NotNull(groups = Post.class)
    @Email(groups = {Default.class, Post.class})
    String email,

    @NotBlank(groups = Post.class)
    @Size(min = 8, max = 128, groups = {Default.class, Post.class})
    String password
) {
}