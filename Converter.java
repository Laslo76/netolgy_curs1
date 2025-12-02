package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    protected int maxWidth;
    protected int maxHeight;
    protected double maxImageRatio; // Максимальное отношение СТОРОН
    protected TextColorSchema colorSchema;

    public Converter(String palette) {
        maxWidth = -1;
        maxHeight = -1;
        maxImageRatio = 0;
        colorSchema = new ColorsSchema(palette);
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {

        // Скачаем картинку из интернета
        BufferedImage img = ImageIO.read(new URL(url));

        double imageRatio = (double) (img.getWidth() / img.getHeight());
        if (maxImageRatio > 0 && imageRatio <= 1 / maxImageRatio && imageRatio >= maxImageRatio) {
            // выбрасываем исключение
            throw new BadImageSizeException(imageRatio, maxImageRatio);
        }

        int newWidth = img.getWidth();
        int newHeight = img.getHeight();
        double ratioResize;

        // ПРОВЕРИМ УМЕЩАЕТСЯ ЛИ КАРТИНКА В ЗАДАННЫЕ ПРЕДЕЛЬНЫЕ РАЗМЕРЫ
        // ПРИ НЕОБХОДИМОСТИ СОЖМЕМ ЕЁ
        if (maxWidth >= 0 && img.getWidth() > maxWidth) {
            ratioResize = (double) (img.getWidth()) / maxWidth;
            newWidth = maxWidth;
            newHeight = (int) (img.getHeight() / ratioResize);
        }
        if (maxHeight >= 0 && newHeight > maxHeight) {
            ratioResize = (double) (newHeight) / maxHeight;
            newHeight = maxHeight;
            newWidth = (int) (newWidth / ratioResize);
        }

        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        // Теперь сделаем её чёрно-белой. Для этого поступим так:
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        // Попросим у этой картинки инструмент для рисования на ней:
        Graphics2D graphics = bwImg.createGraphics();
        // А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженой картинки:
        graphics.drawImage(scaledImage, 0, 0, null);

        // Теперь в bwImg у нас лежит чёрно-белая картинка нужных нам размеров.
        WritableRaster bwRaster = bwImg.getRaster();

        int width = bwImg.getWidth();
        int height = bwImg.getHeight();
        char[][] bufferImage = new char[height][2 * width];

        // так как в псевдографике одна ячейка по вертикали примерно равна двум по горизонтали
        // то в итоговое изображение по строке, символ будем заносить два раза подряд
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = bwRaster.getPixel(x, y, new int[3])[0];
                char c = colorSchema.convert(color);
                bufferImage[y][2 * x] = c;
                bufferImage[y][2 * x + 1] = c;
            }
        }

        // СОБЕРЕМ ИТОГОВЫЙ ТЕКСТ С ПОМОЩЬЮ СТРИНГБИЛДЕРА
        StringBuilder result = new StringBuilder();

        for (char[] row : bufferImage) {
            for (char ch : row) {
                result.append(ch);
            }
            result.append('\n');
        }

        // Возвращаем собранный текст.
        return result.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        maxImageRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        colorSchema = schema;
    }
}
