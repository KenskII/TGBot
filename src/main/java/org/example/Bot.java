package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "";
    private static final String BOT_TOKEN = "";

    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public String getBotToken() {
        return BOT_TOKEN;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendMessage(chatId, "Напиши /help чтобы узнать что я умею.");
                    break;
                case "/help":
                    sendMessage(chatId, """
                            /start - начать работу
                            /help - получить помощь
                            /big_win - взломать казино
                            /cinema - показать киноафишу""");
                    break;
                case "/big_win":
                    sendMessage(chatId, "https://youtu.be/dQw4w9WgXcQ?si=7Jk3XXtue7P64M5p");
                    break;
                case "/cinema":
                    sendCinemaSchedule(chatId);
                    break;
                default:
                    sendMessage(chatId, "Неизвестная команда");
            }
        }
    }

    private void sendCinemaSchedule(long chatId) {
        try {
            List<Parser.MovieSession> sessions = Parser.parseMovieSessions();
            if (sessions.isEmpty()) {
                sendMessage(chatId, "Не удалось получить расписание сеансов.");
                return;
            }

            StringBuilder response = new StringBuilder();
            response.append("<b>Киноафиша на сегодня:</b>\n\n");

            for (Parser.MovieSession session : sessions) {
                response.append(session).append("\n");
            }

            sendMessage(chatId, response.toString());
        } catch (IOException e) {
            sendMessage(chatId, "Ошибка при получении расписания: " + e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.disableWebPagePreview();
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}