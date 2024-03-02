package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.db.MainRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


/**
 * Обработка команды начала работы с ботом
 */
@Service
@AllArgsConstructor
@Slf4j
public class StartCommand implements IBotCommand {

    @Autowired
    private MainRepository repository;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());

        answer.setText("""
                Привет! Данный бот помогает отслеживать стоимость биткоина.
                Поддерживаемые команды:
                 /get_price - получить стоимость биткоина
                 /subscribe N - Подписывает пользователя на стоимость биткоина N
                 /get_subscription - Возвращает текущую подписку
                 /unsubscribe - Отменяет подписку пользователя
                """);
        try {
            repository.registerUser(absSender.getMe().getId(), message.getChatId());
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }
}