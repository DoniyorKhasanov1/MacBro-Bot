package uz.pdp.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Samsung {
    private long id;
    private String name;
    private String color;
    private long userId;
    private Double price;
}
