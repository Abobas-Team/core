package org.core.dnd_ai.security.users;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.core.dnd_ai.global.exception.EmailAlreadyExistsException;
import org.core.dnd_ai.global.exception.UsernameAlreadyExistsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, PasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    public User save(@NonNull User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
        if (user instanceof LocalUser localUser) {
            localUser.setPassword(passwordEncoder.encode(localUser.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Override
    public void resetPassword(ResetPasswordData resetPasswordData, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);

        if (user instanceof LocalUser localUser) {
            var oldPassword = resetPasswordData.oldPassword();
            var currentPassword =  localUser.getPassword();

            if (!passwordEncoder.matches(oldPassword, currentPassword)) {
                throw new IllegalArgumentException("Old password is incorrect");
            }
            localUser.setPassword(passwordEncoder.encode(resetPasswordData.newPassword()));
            userRepository.save(localUser);
        } else {
            throw new IllegalArgumentException("Cannot reset password for OAuth user");
        }
    }
}
