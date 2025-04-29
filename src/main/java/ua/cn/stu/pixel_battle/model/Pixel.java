package ua.cn.stu.pixel_battle.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pixel {
    private int x;
    private int y;
    private String color;
}