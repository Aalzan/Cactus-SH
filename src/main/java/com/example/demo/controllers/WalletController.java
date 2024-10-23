package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final UserService userService;

    @Autowired
    public WalletController(UserService userService) {
        this.userService = userService;
    }

    // Обновление адреса кошелька для текущего пользователя
    @PostMapping("/update")
    public ResponseEntity<String> updateWalletAddress(@RequestBody String newWalletAddress, Authentication authentication) {
        try {
            String username = authentication.getName(); // Получаем имя пользователя из аутентификации
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            userService.updateWalletAddress(user, newWalletAddress); // Обновляем адрес кошелька
            return ResponseEntity.ok("Wallet address updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating wallet address: " + e.getMessage());
        }
    }
}
