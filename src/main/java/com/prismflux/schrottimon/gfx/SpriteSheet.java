package com.prismflux.schrottimon.gfx;

import org.mapeditor.core.TileSet;
import org.mapeditor.io.TMXMapReader;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SpriteSheet {

    public String path;
    public int width;
    public int height;

    public int spriteWidth = 16;
    public int spriteHeight = 16;

    public int[] pixels;

    private TileSet tileset;

    private static final BufferedImage image = null;

    public SpriteSheet(String path, int sprWidth, int sprHeight) {
        this(path);

        this.spriteWidth = sprWidth;
        this.spriteHeight = sprHeight;
    }

    public SpriteSheet(String path) {

        /*try {
            image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        TMXMapReader mr = null;
        //TileSet ts = null;
        try {
            mr = new TMXMapReader();
            tileset = mr.readTileset(SpriteSheet.class.getResourceAsStream(path));
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (image == null) {
            return;
        }*/

        this.path = path;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public BufferedImage getSprite(int index) {
        return tileset.getTile(index).getImage();

        /*int x = index % this.spriteWidth;
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

        return iArray;*/
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
