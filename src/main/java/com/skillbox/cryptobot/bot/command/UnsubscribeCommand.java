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

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    @Autowired
    private MainRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            if(repository.getPrice(absSender.getMe().getId()) == null){
                answer.setText("Активные подписки отсутствуют");
            }
            else {
                repository.setPrice(absSender.getMe().getId(), null);
                answer.setText("Подписка отменена");
            }
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Ошибка возникла в /unsubscribe методе", e);
        }
    }
}