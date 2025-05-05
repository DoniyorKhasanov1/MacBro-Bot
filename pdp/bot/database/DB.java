package uz.pdp.bot.database;

import uz.pdp.bot.entity.Apple;
import uz.pdp.bot.entity.Samsung;

import java.util.ArrayList;
import java.util.List;

public interface DB {
    List<Samsung> samsungList = new ArrayList<>();
    List<Apple> appleList = new ArrayList<>();

     static void addSamsung(Samsung samsung) {
        samsungList.add(samsung);
    }

     static void addApple(Apple apple) {
        appleList.add(apple);
    }

    static List<Samsung> getAllSamsung() {
        return samsungList;
    }

    static List<Apple> getAllApple() {
        return appleList;
    }
    static boolean deleteAppleByName(String text){
         return appleList.removeIf(app -> app.getName().equalsIgnoreCase(text));
    }
    static boolean deleteSamsungByName(String text){
         return samsungList.removeIf(s -> s.getName().equalsIgnoreCase(text));
    }
}
