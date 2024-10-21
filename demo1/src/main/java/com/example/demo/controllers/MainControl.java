package com.example.demo.controllers;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.example.demo.service.UserService.logger;

@Controller
public class MainControl {

    @Autowired
    private UserService userService;

    // Загрузка Alchemy API ключа из .env
    private final Dotenv dotenv = Dotenv.load();
    private final String alchemyApiKey = dotenv.get("ALCHEMY_API_KEY");

    // Инициализация Web3j
    private final Web3j web3j = Web3j.build(new HttpService("https://eth-mainnet.g.alchemy.com/v2/" + alchemyApiKey));

    // Главная страница
    @GetMapping("/home")
    public String mainpage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<User> user = userService.getUserByUsername(username);

        if (user.isPresent()) {
            User currentUser = user.get();

            // Форматирование даты регистрации
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String formattedRegistrationDate = currentUser.getRegistrationDate().format(formatter);

            // Получение баланса кошелька, если адрес есть
            String walletAddress = currentUser.getWalletAddress();
            BigDecimal walletBalance = (walletAddress != null) ? getWalletBalance(walletAddress) : BigDecimal.ZERO;

            // Передача данных в модель
            model.addAttribute("user", currentUser);
            model.addAttribute("formattedRegistrationDate", formattedRegistrationDate);
            model.addAttribute("walletBalance", walletBalance);
        } else {
            return "error"; // Если пользователь не найден
        }

        return "index"; // Возвращаем HTML-страницу
    }

    // Метод для получения баланса кошелька
    private BigDecimal getWalletBalance(String walletAddress) {
        try {
            EthGetBalance ethGetBalance = web3j.ethGetBalance(walletAddress, org.web3j.protocol.core.DefaultBlockParameterName.LATEST).send();
            BigInteger wei = ethGetBalance.getBalance();
            return Convert.fromWei(wei.toString(), Convert.Unit.ETHER);
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @PostMapping("/connect_wallet")
    @ResponseBody
    public String connectWallet(@RequestBody WalletRequest request) {
        logger.debug("Received wallet connection request for address: {}", request.getAddress());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        logger.debug("Authenticated user: {}", username);

        Optional<User> user = userService.getUserByUsername(username);

        if (user.isPresent()) {
            User currentUser = user.get();

            currentUser.setWalletAddress(request.getAddress());
            userService.save(currentUser);

            logger.debug("Wallet address saved for user: {}", username);

            return "{\"status\": \"success\", \"message\": \"Wallet connected successfully.\"}";
        } else {
            logger.error("User not found for username: {}", username);
            return "{\"status\": \"error\", \"message\": \"User not found.\"}";
        }
    }



    // Класс для приема запроса на подключение кошелька
    public static class WalletRequest {
        private String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
