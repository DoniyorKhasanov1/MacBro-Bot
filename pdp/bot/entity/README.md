## Multithreading nima?
*** 
Multithreading – bu bir jarayon ichida bir nechta thread yaratib, ularni bir vaqtda yoki
parallel ravishda bajarish jarayoni. Threadlar bitta dastur (process) doirasidagi kichik
vazifalar bo‘lib, ular mustaqil ishlaydi, lekin umumiy resurslarni (masalan, xotira) birgalikda
ishlatadi.
```java
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
    
```
- dsdsds
- fdfsfsdfsfsfs
