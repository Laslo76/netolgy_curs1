package ru.netology.graphics.image;

import java.util.Arrays;

public class ColorsSchema implements TextColorSchema {
    protected int stepColor;
    protected char[] schema;

    public ColorsSchema(String palitra) {
        if (palitra.isEmpty()) {
            this.schema = "'-+*%@$#".toCharArray();
        } else {
            this.schema = palitra.toCharArray();
        }
        this.stepColor = 255 / this.schema.length;
    }

    public ColorsSchema(String colors, boolean revers) {

    }

    @Override
    public char convert(int color) {
        return schema[color / stepColor];
    }

}
