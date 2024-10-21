package com.example.demo.controllers;

import com.example.demo.service.UserService;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogAndRegBoba {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public LogAndRegBoba(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth";  // Возвращаем страницу логина
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password) {
        // Загрузка пользователя по имени
        User user = (User) userService.loadUserByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return "redirect:/home";  // Перенаправление на /continue
        } else {
            return "redirect:/login?error=true";  // Возвращаем ошибку
        }
    }
}
