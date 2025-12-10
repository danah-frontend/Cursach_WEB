package ru.rutmiit.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import ru.rutmiit.repositories.UserRepository;
import ru.rutmiit.services.AppUserDetailsService;

@Slf4j
@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
        log.info("SecurityConfig инициализирован");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SecurityContextRepository securityContextRepository) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Статические ресурсы доступны всем
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/favicon.ico", "/error").permitAll()

                        // Публичные страницы
                        .requestMatchers("/", "/home", "/about", "/contact").permitAll()
                        .requestMatchers("/users/login", "/users/register", "/users/login-error").permitAll()
                        .requestMatchers("/products/all", "/products/product-details/**").permitAll()

                        // API для Redis
                        .requestMatchers("/admin/cache/**").permitAll()

                        // КОРЗИНА - только для обычных пользователей (не админов/модераторов)
                        .requestMatchers("/cart/**").hasRole("USER")

                        // МОИ ЗАКАЗЫ (/orders) - только для обычных пользователей
                        .requestMatchers("/orders").authenticated()

                        // ДЕТАЛИ КОНКРЕТНОГО ЗАКАЗА - доступны всем авторизованным
                        .requestMatchers("/orders/{id}").authenticated()

                        // ОТМЕНА ЗАКАЗА - доступна всем авторизованным
                        .requestMatchers("/orders/cancel/**").authenticated()

                        // Админские страницы (только для ADMIN)
                        .requestMatchers("/products/add", "/products/delete/**", "/products/confirm-delete/**")
                        .hasAnyRole("ADMIN","MODERATOR")

                        // Страницы для админов и модераторов (управление заказами)
                        .requestMatchers("/orders/admin", "/orders/admin/**")
                        .hasAnyRole("ADMIN", "MODERATOR")

                        // Профиль доступен всем авторизованным
                        .requestMatchers("/users/profile").authenticated()

                        // Все остальные страницы
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/users/login")
                        .usernameParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY)
                        .passwordParameter(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY)
                        .defaultSuccessUrl("/", true)
                        .failureForwardUrl("/users/login-error")
                        .permitAll()
                )
                // ВАЖНО: отключаем обработку ошибок доступа через страницу
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            // Просто возвращаем 403 без редиректа
                            response.sendError(403, "Доступ запрещен");
                        })
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400 * 7) // 7 дней
                        .userDetailsService(userDetailsService())
                        .rememberMeParameter("remember-me")
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(securityContextRepository)
                )
                .csrf(csrf -> csrf.disable());

        log.info("SecurityFilterChain настроен");
        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(),
                new HttpSessionSecurityContextRepository()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new AppUserDetailsService(userRepository);
    }
}