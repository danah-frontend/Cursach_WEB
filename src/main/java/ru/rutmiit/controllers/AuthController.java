package ru.rutmiit.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.UserRegistrationDto;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.services.AuthService;
import ru.rutmiit.dto.UserProfileView;

import java.security.Principal;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/users")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
        log.info("AuthController инициализирован");
    }

    @ModelAttribute("userRegistrationDto")
    public UserRegistrationDto initForm() {
        return new UserRegistrationDto();
    }

    @GetMapping("/register")
    public String register() {
        log.debug("Отображение страницы регистрации");
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid UserRegistrationDto userRegistrationDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        log.debug("Обработка регистрации пользователя: {}", userRegistrationDto.getEmail());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при регистрации: {}", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationDto", bindingResult);

            return "redirect:/users/register";
        }

        try {
            this.authService.register(userRegistrationDto);
            log.info("Пользователь успешно зарегистрирован: {}", userRegistrationDto.getEmail());
            return "redirect:/users/login";
        } catch (RuntimeException e) {
            log.error("Ошибка при регистрации: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("userRegistrationDto", userRegistrationDto);
            return "redirect:/users/register";
        }
    }

    @GetMapping("/login")
    public String login() {
        log.debug("Отображение страницы входа");
        return "login";
    }

    @PostMapping("/login-error")
    public String onFailedLogin(
            @ModelAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY) String username,
            RedirectAttributes redirectAttributes) {

        log.warn("Неудачная попытка входа для пользователя: {}", username);
        redirectAttributes.addFlashAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY, username);
        redirectAttributes.addFlashAttribute("badCredentials", true);

        return "redirect:/users/login";
    }

    @GetMapping("/profile")
    public String profile(Principal principal, Model model) {
        String email = principal.getName();
        log.debug("Отображение профиля пользователя: {}", email);

        User user = authService.getUser(email);

        String roles = user.getRoles().stream()
                .map(role -> role.getName().getDisplayName())
                .collect(Collectors.joining(", "));

        UserProfileView userProfileView = new UserProfileView(
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                roles
        );

        model.addAttribute("user", userProfileView);
        model.addAttribute("cartItemCount", 0); // Добавьте CartService при необходимости

        return "profile";
    }
}
