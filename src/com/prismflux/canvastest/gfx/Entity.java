package com.prismflux.canvastest.gfx;

import io.socket.client.Socket;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;

import static com.prismflux.canvastest.net.SocketConnection.getSocket;

public class Entity implements Renderable {

    private int entityX;
    private int entityY;
    public int width = 32;
    public int height = 32;
    private Level level;
    private String path;

    public final boolean isPlayer = false;

    BufferedImage image = null;

    public Entity(Socket socket, Level level, String path, int x, int y) {
        this.level = level;
        this.path = path;
        entityX = x;
        entityY = y;

        try {
            image = ImageIO.read(Entity.class.getResourceAsStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image == null) {
            System.out.println("Failed to load Entity Image for " + path);
            return;
        }
    }

    public int getEntityX() {
        return entityX;
    }

    public int getEntityY() {
        return entityY;
    }

    protected void setEntityX(int x) {
        entityX = x;
    }

    protected void setEntityY(int y) {
        entityY = y;
    }

    protected void moveUp() {
        if (level.canWalkThere(this.entityX, this.entityY - 1)) {
            getSocket().emit("moveUp");
            entityY--;
        }
    }

    protected void moveDown() {
        if (level.canWalkThere(this.entityX, this.entityY + 1)) {
            entityY++;
            getSocket().emit("moveDown");
        }
    }

    protected void moveLeft() {
        if (level.canWalkThere(this.entityX - 1, this.entityY)) {
            getSocket().emit("moveLeft");
        }
    }

    protected void moveRight() {
        if (level.canWalkThere(this.entityX + 1, this.entityY)) {
            getSocket().emit("moveRight");
        }
    }

    @Override
    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {
        Graphics g = image.getGraphics();
        g.setColor(new Color(127, 127, 255, 127));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
    }

    @Override
    public void draw(int[] pixels, BufferedImage image, int offset, int row) {
        image.setRGB(0, 0, width, height, getSprite(), 0, height);
    }

    @Override
    public void update(double delta) {

    }

    public int[] getSprite() {
        int[] p = new int[32 * 32];
        image.getRGB(0, 0, 32, 32, p, 0, 32);

        return p;
    }
}
