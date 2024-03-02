package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.db.MainRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    @Autowired
    private MainRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        try {
            BigDecimal price = repository.getPrice(absSender.getMe().getId());
            answer.setText(price == null ? "Активные подписки отсутствуют" : "Вы подписаны на стоимость биткоина " + price + " USD");
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла в /get_subscription методе", e);
        }
    }
}