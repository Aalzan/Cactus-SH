package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    public static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private String walletAddress;



    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }





    public User getCurrentUser() {
        logger.debug("Getting current user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
    public void registerUser(User user, String walletAddress) throws Exception {
        // Проверка на наличие пользователя с таким же именем или email
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new Exception("Username is already taken");
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email is already registered");
        }

        // Устанавливаем дату регистрации
        user.setRegistrationDate(LocalDateTime.now());

        // Шифрование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Устанавливаем walletAddress как null при регистрации
        user.setWalletAddress(null);

        // Сохранение пользователя в базе данных
        userRepository.save(user);
    }


    public void updateWalletAddress(User user, String newWalletAddress) {
        if (newWalletAddress == null || newWalletAddress.isEmpty()) {
            throw new IllegalArgumentException("Wallet address cannot be null or empty");
        }
        user.setWalletAddress(newWalletAddress); // Устанавливаем новый адрес кошелька
        userRepository.save(user); // Сохраняем обновленного пользователя в базу данных
    }
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")  // Установите роли, если нужно
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public void save(User currentUser) {
    }
}
