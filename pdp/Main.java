package uz.pdp;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import uz.pdp.bot.service.MyBot;

import java.util.ResourceBundle;

public class Main {
    public static ResourceBundle bundle = ResourceBundle.getBundle("settings");
    public static void main(String[] args) {
        try{
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyBot(bundle.getString("BOT_TOKEN"), "@macbook_uz_bot"));
            System.err.println("The bot has been started...");
        }catch (TelegramApiException e){
            e.printStackTrace();
        }
    }
}
