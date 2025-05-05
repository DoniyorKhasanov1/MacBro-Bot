package uz.pdp.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Apple {
    private long id;
    private String name;
    private String color;
    private Double price;
    private long userId;
}
