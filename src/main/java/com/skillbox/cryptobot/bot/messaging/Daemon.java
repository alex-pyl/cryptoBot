package com.skillbox.cryptobot.bot.messaging;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.db.MainRepository;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
@Configuration
@EnableScheduling
public class Daemon {

    @Autowired
    private MainRepository repository;
    private final CryptoCurrencyService service;
    private final CryptoBot bot;

    @Value("${telegram.bot.notify.delay.value}")
    private String value;

    @Value("${telegram.bot.notify.delay.unit}")
    private String unit;

    @Scheduled(fixedDelayString = "${telegram.bot.notify.period}")
    public void notification() throws IOException {
        System.out.println("notification");
        double price = service.getBitcoinPrice();
        for(Long chat: repository.notificationList(price, Integer.parseInt(value), unit)){
            bot.sendMessage(chat, "Пора покупать, стоимость биткоина " + price);
        }
    }

}
