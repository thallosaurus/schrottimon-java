package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.net.SocketConnection;
import org.tiledreader.TiledReader;

import static com.prismflux.canvastest.net.SocketConnection.getSocket;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

public class Level implements Renderable, Animatable {

    public final int width = 5;
    public final int height = 6;

    private ArrayList<Entity> entities = new ArrayList<>();

    public final String path;

    public float xOffset = 0;
    public float yOffset = 0;

    public final SpriteSheet sheet;

    private final int[] map = {
            0, 1, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            1, 1, 1, 1, 1
    };

    public Level(String path) {
        this.path = path;

        //load tmx file here
        //set level width/height according to tmx file
        //load spritesheet here
        this.sheet = new SpriteSheet("/cuterpg/Tiles.png");

        //addEntity(new Player(this, 3, 3));

        //addEntity(new Entity(getSocket(), this, "/entities/Base.png", 1, 1));
        //addEntity(new Entity(getSocket(), this, "/entities/Base.png", 1, 2));
        //addEntity(new Entity(getSocket(), this, "/entities/Base.png", 1, 3));

        Animation.animationQueue.add(this);

        //nothing to do, because tmx isnt implemented yet
    }

    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {
        int pixelsWidth = row;
        int pixelsHeight = pixels.length / pixelsWidth;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = XYtoIndex(x, y);
                int[] sprite = sheet.getSpriteDebug(map[index]);

                /*if (getPlayerX() == x && getPlayerY() == y) {
                    for (int i = 0; i < sprite.length; i++) {
                        sprite[i] &= 0xffff00;
                    }
                }*/

                image.setRGB(x * sheet.spriteWidth + getXOffset(), y * sheet.spriteHeight + getYOffset(), sheet.spriteWidth, sheet.spriteHeight, sprite, 0, sheet.spriteHeight);
            }
        }

        drawEntitiesDebug(image);
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    //public static Player player;

    private Player getPlayer() {
        //if (player != null) return player;

        for (int i = 0; i < entities.size(); i++) {
            Player e = (Player) entities.get(i);
            if (e.isPlayer) {
                return (Player) e;
            }
        }

        return null;
    }

    private int getPlayerY() {
        if (getPlayer() != null) {
            return getPlayer().getEntityY();
        }

        return 0;
    }

    private int getPlayerX() {
        if (getPlayer() != null) {
            return getPlayer().getEntityX();
        }

        return 0;
    }

    @Override
    public void draw(int[] pixels, BufferedImage image, int offset, int row) {

        int pixelsWidth = row;
        int pixelsHeight = pixels.length / pixelsWidth;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = XYtoIndex(x, y);
                int[] sprite = sheet.getSprite(map[index]);

                image.setRGB(x * sheet.spriteWidth + getXOffset(), y * sheet.spriteHeight + getYOffset(), sheet.spriteWidth, sheet.spriteHeight, sprite, 0, sheet.spriteHeight);

                //final int fX = x;
                //final int fY = y;
                if (getPlayer() != null) {
                    if (getPlayerX() == x && getPlayerY() == y) {
                        for (int i = 0; i < sprite.length; i++) {

                            image.setRGB(
                                    x * sheet.spriteWidth,
                                    y * sheet.spriteHeight,
                                    sheet.spriteWidth,
                                    sheet.spriteHeight,
                                    getPlayer().getSprite(),
                                    0,
                                    32
                            );
                        }
                    }
                }

              }
        }

        drawEntities(image);

        image.flush();
    }

    private void drawEntities(BufferedImage image) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            BufferedImage img = image.getSubimage(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height);
            int[] entPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
            e.draw(entPixels, img, 0, width);

            //image.setRGB(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height, entPixels, 0, e.height);
            img.flush();
        }
    }

    private void drawEntitiesDebug(BufferedImage image) {
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            BufferedImage img = image.getSubimage(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height);
            int[] entPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

            e.drawDebug(entPixels, img, 0, width);

            //image.setRGB(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height, entPixels, 0, e.height);
            img.flush();
        }
    }

    public boolean canWalkThere(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return false;
        }

        return true;
    }

    int XYtoIndex(int x, int y) {
        return (y * this.width) + x;
    }

    @Override
    public void setXOffset(float offset) {
        this.xOffset = offset;
    }

    @Override
    public void setYOffset(float offset) {
        this.yOffset = offset;
    }

    @Override
    public int getXOffset() {
        return (int) xOffset * sheet.spriteWidth;
    }

    @Override
    public int getYOffset() {
        return (int) yOffset * sheet.spriteHeight;
    }

    public void update(double delta) {

    }
}
