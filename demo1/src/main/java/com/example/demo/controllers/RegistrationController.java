package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registrationPage() {
        return "auth";  // Возвращаем страницу регистрации
    }


    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam("walletAddress") String walletAddress) {
        try {
            userService.registerUser(user, walletAddress);
            return "redirect:/login"; // Редирект на страницу входа после успешной регистрации
        } catch (Exception e) {
            e.printStackTrace();
            return "error"; // Обработка ошибки
        }
    }

}
