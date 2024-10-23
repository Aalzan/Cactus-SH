package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ShopController {

    // Обработка GET-запроса для shop.html
    @GetMapping("/shop")
    public String showShopPage() {
        return "shop"; // Возвращаем название шаблона shop.html
    }
}
