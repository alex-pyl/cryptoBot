package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.db.MainRepository;
import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.math.BigDecimal;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class SubscribeCommand implements IBotCommand {

    private final CryptoCurrencyService service;
    @Autowired
    private MainRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        String price = message.getText();
        int start = price.indexOf(getCommandIdentifier() + " ");
        if(start < 0){
            price = "error";
        }
        else {
            price = price.substring(start + getCommandIdentifier().length() + 1);
        }
        BigDecimal realPrice;
        try {
            realPrice = new BigDecimal(price);
        } catch (Exception ex) {
            try {
                answer.setText("В команде /" + getCommandIdentifier() + " введите число после пробела");
                absSender.execute(answer);
            } catch (Exception e) {
                log.error("Ошибка возникла в /subscribe методе", e);
            }
            return;
        }
        try {
            repository.setPrice(absSender.getMe().getId(), realPrice);
            answer.setText("Текущая цена биткоина " + TextUtil.toString(service.getBitcoinPrice()) + " USD");
            absSender.execute(answer);
            answer.setText("Новая подписка создана на стоимость " + price + " USD");
            absSender.execute(answer);
        } catch (Exception e) {
            log.error("Ошибка возникла в /subscribe методе", e);
        }

    }
}