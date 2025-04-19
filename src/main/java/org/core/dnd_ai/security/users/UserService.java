package org.core.dnd_ai.security.users;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User save(@NonNull User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            // TODO: Once GlobalExceptionHandler is implemented, change to exception.
            System.out.println("This email is already registered");
            return null;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
