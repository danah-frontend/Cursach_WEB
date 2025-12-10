package ru.rutmiit.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.UserRegistrationDto;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.enums.UserRoles;
import ru.rutmiit.repositories.UserRepository;
import ru.rutmiit.repositories.UserRoleRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       UserRoleRepository userRoleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(UserRegistrationDto registrationDto) {
        // Проверка email
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email уже используется: " + registrationDto.getEmail());
        }

        // Проверка паролей
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }

        // Получаем роль USER
        var userRole = userRoleRepository.findByName(UserRoles.USER)
                .orElseThrow(() -> new RuntimeException("Роль USER не найдена"));

        // Создаем пользователя
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setName(registrationDto.getName());
        user.setPhone(registrationDto.getPhone());
        user.setRoles(List.of(userRole));

        // Сохраняем
        userRepository.save(user);
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " не найден"));
    }
}