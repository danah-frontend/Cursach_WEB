package ru.rutmiit.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.rutmiit.repositories.UserRepository;

import java.util.stream.Collectors;

public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(u -> new User(
                        u.getEmail(), // Используем email как username
                        u.getPassword(),
                        u.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                                .collect(Collectors.toList())
                )).orElseThrow(() -> new UsernameNotFoundException(username + " не найден!"));
    }
}