package com.example.demo.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private Set<String> roles;
    private LocalDateTime registrationDate;

    // Добавляем поле для адреса кошелька
    private String walletAddress;

    // Геттеры и сеттеры

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public DateFormat getLastLoginDate() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public String getEmail() {
        return email;
    }

    // Новый геттер для адреса кошелька
    public String getWalletAddress() {
        return walletAddress;
    }

    // Новый сеттер для адреса кошелька
    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }
}
