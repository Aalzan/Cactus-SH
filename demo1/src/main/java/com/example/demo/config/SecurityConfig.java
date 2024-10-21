package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomOAuth2UserService;
import com.example.demo.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, @Lazy UserService userService, UserRepository userRepository) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/register", "/oauth2/**", "/static/**").permitAll()  // Разрешаем доступ к статическим ресурсам и OAuth2
                        .anyRequest().authenticated()  // Остальные запросы требуют аутентификации
                )
                .formLogin(form -> form
                        .loginPage("/login")  // Страница логина
                        .defaultSuccessUrl("/home", true)  // Перенаправление после успешного входа
                        .failureUrl("/login?error=true")  // Страница в случае ошибки входа
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // Страница для OAuth2 логина
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))  // Настройка кастомного OAuth2 сервиса
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")  // Перенаправление после выхода
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Шифрование паролей через BCrypt
    }

    // Получение текущего пользователя
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }
}
