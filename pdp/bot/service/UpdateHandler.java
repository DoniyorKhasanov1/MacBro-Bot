package uz.pdp.bot.service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.bot.database.DB;
import uz.pdp.bot.entity.AdminState;
import uz.pdp.bot.entity.Apple;
import uz.pdp.bot.entity.Samsung;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateHandler {
    private final Map<String, AdminState> adminStates = new HashMap<>();
    private final Map<String, String> selectedBrand = new HashMap<>();
    private final Map<String, Samsung> tempSamsung = new HashMap<>();
    private final Map<String, Apple> tempApple = new HashMap<>();
    private final List<String> colors = Arrays.asList("TITANIUM", "WHITE", "BLACK", "GOLD");

    private final MyBot bot;

    public UpdateHandler(MyBot bot) {
        this.bot = bot;
    }

    public void adminHandler(String chatId, Message message) throws TelegramApiException {
        String text = message.getText();
        if (text.equals("/start")) {
            SendMessage message1 = new SendMessage(chatId, "Hello admin👨‍💻\nHow's it going?! May I give you the panel?📱");
            bot.execute(message1);
            askAdminPanel(chatId, "Here's the panel for you: ⬇️");
        } else if (text.equals("Admin Panel📱")) {
            fullAdminPanel(chatId);
        } else if (text.equals("➕Add product")) {
            SendMessage message1 = new SendMessage(chatId, "Select the type of product you want to add⬇️");
            ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
            markup.setSelective(true);
            markup.setResizeKeyboard(true);
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton("📱Samsung"));
            row.add(new KeyboardButton("📱Apple"));
            markup.setKeyboard(List.of(row));
            message1.setReplyMarkup(markup);
            adminStates.put(chatId, AdminState.CHOOSING_BRAND);
            bot.execute(message1);


        } else if (adminStates.get(chatId) == AdminState.CHOOSING_BRAND) {
            if (text.equals("📱Samsung") || text.equals("📱Apple")) {
                SendMessage removeKeyboardMessage = new SendMessage(chatId, "✅ Type selected: " + text);
                ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                removeKeyboard.setRemoveKeyboard(true);
                removeKeyboardMessage.setReplyMarkup(removeKeyboard);
                bot.execute(removeKeyboardMessage);
                selectedBrand.put(chatId, text.replace("📱", ""));
                if (text.contains("Samsung"))
                    tempSamsung.put(chatId, new Samsung());
                else
                    tempApple.put(chatId, new Apple());
                adminStates.put(chatId, AdminState.WRITING_MODEL);
                bot.execute(new SendMessage(chatId, "Please enter model name:"));
            }
        } else if (adminStates.containsKey(chatId)) {
            AdminState state = adminStates.get(chatId);
            switch (state) {
                case WRITING_MODEL -> {
                    String brand = selectedBrand.get(chatId);
                    if (brand.equals("Samsung")) {
                        Samsung samsung = tempSamsung.get(chatId);
                        samsung.setName(text);
                    } else if (brand.equals("Apple")) {
                        Apple apple = tempApple.get(chatId);
                        apple.setName(text);
                    }
                    adminStates.put(chatId, AdminState.WRITING_PRICE);
                    sendMessage(chatId, "💸Please enter price: ");
                }
                case DELETE_PRODUCT -> {
                    SendMessage removeKeyboardMessage = new SendMessage(chatId, "Deleting product");
                    ReplyKeyboardRemove removeKeyboard = new ReplyKeyboardRemove();
                    removeKeyboard.setRemoveKeyboard(true);
                    removeKeyboardMessage.setReplyMarkup(removeKeyboard);
                    bot.execute(removeKeyboardMessage);
                    boolean removed = DB.deleteSamsungByName(text) || DB.deleteAppleByName(text);
                    if (removed) {
                        sendMessage(chatId, "✅Product deleted successfully");
                    } else {
                        sendMessage(chatId, "❌Error occurred while deleting");
                    }
                    adminStates.remove(chatId);
                }
                case WRITING_PRICE -> {
                    String brand = selectedBrand.get(chatId);
                    try {
                        double price = Double.parseDouble(text);
                        if (brand.equals("Samsung")) {
                            Samsung samsung = tempSamsung.get(chatId);
                            samsung.setPrice(price);
                        } else if (brand.equals("Apple")) {
                            Apple apple = tempApple.get(chatId);
                            apple.setPrice(price);
                        }
                        adminStates.put(chatId, AdminState.WRITING_COLOR);
                        choosingProductColor(chatId, "🔵🟢Please choose product color: ");
                    } catch (NumberFormatException e) {
                        sendMessage(chatId, "❌Please enter only numbers");
                    }
                }
                case WRITING_COLOR -> {
                    if (!colors.contains(text)) {
                        sendMessage(chatId, "❌No such color found");
                        return;
                    }
                    String brand = selectedBrand.get(chatId);
                    if (brand.equals("Samsung")) {
                        Samsung samsung = tempSamsung.get(chatId);
                        samsung.setColor(text);
                        DB.addSamsung(samsung);
                        sendMessage(chatId, "✅Samsung product added: \n" +
                                "Model: " + samsung.getName() + "\n" +
                                "Price: " + samsung.getPrice() + "\n" +
                                "Color: " + samsung.getColor());
                        tempSamsung.remove(chatId);
                    } else if (brand.equals("Apple")) {
                        Apple apple = tempApple.get(chatId);
                        apple.setColor(text);
                        DB.addApple(apple);
                        sendMessage(chatId, "✅Apple product added: \n" +
                                "Model: " + apple.getName() + "\n" +
                                "Price: " + apple.getPrice() + "\n" +
                                "Color: " + apple.getColor());
                        tempApple.remove(chatId);
                    }
                    selectedBrand.remove(chatId);
                    adminStates.remove(chatId);
                    fullAdminPanel(chatId);

                }
            }

        } else if (text.equals("📜Show products")) {
            adminStates.put(chatId, AdminState.SHOWING_PRODUCT);
            SendMessage removeKeyboard = new SendMessage(chatId, "Products will be displayed❗");
            ReplyKeyboardRemove remove = new ReplyKeyboardRemove();
            remove.setRemoveKeyboard(true);
            removeKeyboard.setReplyMarkup(remove);
            bot.execute(removeKeyboard);
            showAllProducts(chatId);
            adminPanel(chatId);

        } else if (text.equals("➖Delete product")) {
            adminStates.put(chatId, AdminState.DELETE_PRODUCT);
            sendMessage(chatId, "📱Enter the name of product you want to delete: ");
        } else {
            sendMessage(chatId, "Could not understand this command☹️");
        }
    }

    public void askAdminPanel(String chatId, String text) throws TelegramApiException {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardButton button = new KeyboardButton("Admin Panel📱");
        KeyboardRow row = new KeyboardRow();
        row.add(button);
        markup.setKeyboard(List.of(row));
        markup.setResizeKeyboard(true);
        SendMessage message = new SendMessage(chatId, text);
        message.setReplyMarkup(markup);
        bot.execute(message);
    }

    public void fullAdminPanel(String chatId) throws TelegramApiException {
        adminPanel(chatId);
    }

    private void adminPanel(String chatId) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, "Manage the full service using these buttons below⬇️");
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton addProduct = new KeyboardButton("➕Add product");
        KeyboardButton deleteProduct = new KeyboardButton("➖Delete product");
        row1.add(addProduct);
        row1.add(deleteProduct);
        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton showProduct = new KeyboardButton("📜Show products");
        row2.add(showProduct);
        markup.setKeyboard(Arrays.asList(row1, row2));
        message.setReplyMarkup(markup);
        bot.execute(message);
    }

    public void choosingProductColor(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        row1.add(new KeyboardButton("TITANIUM"));
        row1.add(new KeyboardButton("BLACK"));
        row2.add(new KeyboardButton("WHITE"));
        row2.add(new KeyboardButton("GOLD"));
        markup.setKeyboard(Arrays.asList(row1, row2));
        message.setReplyMarkup(markup);
        markup.setResizeKeyboard(true);
        bot.execute(message);
    }

    public void showAllProducts(String chatId) throws TelegramApiException {
        List<Samsung> samsungList = DB.getAllSamsung();
        List<Apple> appleList = DB.getAllApple();

        if (samsungList.isEmpty() && appleList.isEmpty()) {
            sendMessage(chatId, "📭 No products found.");
            return;
        }

        StringBuilder builder = new StringBuilder("🛒 Available Products:\n\n");

        if (!samsungList.isEmpty()) {
            builder.append("📱 Samsung:\n");
            for (Samsung s : samsungList) {
                builder.append("▪️ Model: ").append(s.getName()).append("\n");
                builder.append("💸 Price: $").append(s.getPrice()).append("\n");
                builder.append("🎨 Color: ").append(s.getColor()).append("\n\n");
            }
        }
        if (!appleList.isEmpty()) {
            builder.append("🍏 Apple:\n");
            for (Apple a : appleList) {
                builder.append("▪️ Model: ").append(a.getName()).append("\n");
                builder.append("💸 Price: $").append(a.getPrice()).append("\n");
                builder.append("🎨 Color: ").append(a.getColor()).append("\n\n");
            }
        }

        sendMessage(chatId, builder.toString());
    }

    public void userHandler(String chatId, Message message) throws TelegramApiException {
        String text = message.getText();
        final var firstName = message.getFrom().getFirstName();
        if (text.equals("/start")){
            sendMessage(chatId, "Hello " + firstName + "👋\n Are you looking for a convenient market to purchase devices from Apple or Samsung❓ \nYou are on the right platform✅");
            languageInlineKeyboard(chatId);
        }

    }

    public void userCallbackQuery(Long chatId, CallbackQuery query) {
        if (query.getData().equals("lan_uz")) {
            uzbekService(chatId);
        }

    }

    public static void uzbekService(Long chatId){

    }
    public void languageInlineKeyboard(String chatId) {
        SendMessage message = new SendMessage(chatId, "🌐Firstly, you should choose your native language❗\nChoose one, and let's move forward⬇️");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        var buttons = getInlineKeyboardButtons();

        message.setReplyMarkup(markup);

        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage(chatId, text);
        bot.execute(message);
    }

    private static List<List<InlineKeyboardButton>> getInlineKeyboardButtons() {
        InlineKeyboardButton buttonUz = new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDFFO'zbek");
        buttonUz.setCallbackData("lan_uz");
        InlineKeyboardButton buttonEn = new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDF8English");
        buttonEn.setCallbackData("lan_en");
        InlineKeyboardButton buttonRu = new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFAРусский");
        buttonRu.setCallbackData("lan_ru");
        List<InlineKeyboardButton> row = List.of(buttonUz, buttonEn, buttonRu);
        return List.of(row);
    }

}
