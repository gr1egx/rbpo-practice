package ru.mtuci.rbpo_practice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_practice.models.ApplicationUser;
import ru.mtuci.rbpo_practice.models.UserDetailsImpl;
import ru.mtuci.rbpo_practice.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ApplicationUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));
        return UserDetailsImpl.fromApplicationUser(user);
    }

    public Optional<ApplicationUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<ApplicationUser> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
