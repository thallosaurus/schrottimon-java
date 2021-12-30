package com.prismflux.canvastest.gfx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.IOException;

public class SpriteSheet {

    public String path;
    public int width;
    public int height;

    public int spriteWidth = 32;
    public int spriteHeight = 32;

    public int[] pixels;

    private static BufferedImage image = null;

    public SpriteSheet(String path, int sprWidth, int sprHeight) {
        this(path);

        this.spriteWidth = sprWidth;
        this.spriteHeight = sprHeight;
    }

    public SpriteSheet(String path) {

        try {
            image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image == null) {
            return;
        }

        this.path = path;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public int[] getSprite(int index) {
        int x = index % this.spriteWidth;
        int y = index / this.spriteHeight;

        if (image == null) {
            System.out.println("Spritesheet was null");
            return getSpriteDebug(index);
        }

        BufferedImage subPicture;
        synchronized (image) {
            subPicture = image.getSubimage(x * spriteWidth, y * spriteHeight, this.spriteWidth, this.spriteHeight);
        }

        int[] iArray = new int[this.spriteWidth*this.spriteHeight];

        subPicture.getRGB(0, 0, this.spriteWidth, this.spriteHeight, iArray,0 ,this.spriteHeight);
        subPicture.flush();

        return iArray;
    }

    public int[] getSpriteDebug(int index) {
        BufferedImage img = new BufferedImage(this.spriteWidth, this.spriteHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();

        switch (index) {
            case 0:
            default:
                g.setColor(Color.MAGENTA);
                break;

            case 1:
                g.setColor(Color.BLUE);
                break;
        }

        g.fillRect(0, 0, this.spriteWidth, this.spriteHeight);

        g.setColor(Color.RED);
        g.drawLine(0, 0, this.spriteWidth, this.spriteHeight);
        g.drawLine(0, this.spriteHeight, this.spriteWidth, 0);

        g.setColor(Color.GREEN);
        g.drawRect(0, 0, spriteWidth, spriteHeight);

        g.drawString("" + index, 0, 0);

        g.dispose();

        int[] data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        img.flush();

        return data;

    }

    public int[] getPixels() {
        return pixels;
    }
}
