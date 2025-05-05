package uz.pdp.bot.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ResourceBundle;

import static uz.pdp.Main.bundle;

public class MyBot extends TelegramLongPollingBot {
    public UpdateHandler handler = new UpdateHandler(this);
    public String username;
    public MyBot(String botToken, String username){
        super(botToken);
        this.username = username;
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage() != null){
            String chatId = update.getMessage().getChatId().toString();
            Message message = update.getMessage();
            if (chatId.equals(bundle.getString("ADMIN_ID")) || chatId.equals(bundle.getString("ADMIN_ID2"))){
                try {
                    handler.adminHandler(chatId, message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    handler.userHandler(chatId, message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            handler.userCallbackQuery(chatId, callbackQuery);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }
}
