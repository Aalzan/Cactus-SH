package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // Делегируем загрузку пользователя стандартному сервису
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Определяем провайдера (google, github)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName;

        // Определяем имя атрибута для username в зависимости от провайдера
        if ("github".equalsIgnoreCase(registrationId)) {
            userNameAttributeName = "login";  // GitHub использует 'login'
        } else if ("google".equalsIgnoreCase(registrationId)) {
            userNameAttributeName = "email";  // Google использует 'email'
        } else {
            userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                    .getUserInfoEndpoint().getUserNameAttributeName();
        }

        // Получаем атрибуты пользователя
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Логирование атрибутов для отладки
        logger.debug("Attributes для {}: {}", registrationId, attributes);

        // Извлекаем username и email
        Object usernameObj = attributes.get(userNameAttributeName);
        String username = usernameObj != null ? usernameObj.toString() : null;
        String email = (String) attributes.get("email"); // Предполагаем, что email присутствует

        if (username == null) {
            logger.error("Атрибут username не найден для провайдера: {}", registrationId);
            throw new UsernameNotFoundException("Атрибут username не найден");
        }

        // Ищем пользователя в базе данных или создаем нового
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email != null ? email : "");  // Используем email, если он есть
                    newUser.setPassword("");  // Пароль не требуется для OAuth пользователей
                    newUser.setRegistrationDate(LocalDateTime.now());
                    newUser.setRoles(Collections.singleton("ROLE_USER")); // Назначаем роль

                    logger.debug("Создание нового пользователя с username: {}", username);
                    return userRepository.save(newUser);
                });

        // Преобразуем роли пользователя в GrantedAuthority
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map((Object role) -> new SimpleGrantedAuthority((String) role))
                .collect(Collectors.toSet());

        // Возвращаем DefaultOAuth2User с авторитетами и атрибутами
        return new DefaultOAuth2User(
                authorities,
                attributes,
                userNameAttributeName
        );
    }
}
