package org.core.dnd_ai.security.users;

public interface PasswordService {
    void resetPassword(ResetPasswordData resetPasswordData, String username);
}
