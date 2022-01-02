package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.net.SocketConnection;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.mapeditor.core.Map;
import org.mapeditor.core.TileLayer;
import org.tiledreader.TiledLayer;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

import static com.prismflux.canvastest.net.SocketConnection.getSocket;

public class Entity extends SocketConnection implements Renderable, Emitter.Listener {

    private int entityX;
    private int entityY;
    public int width = 32;
    public int height = 32;
    //private Level level;
    private String path;

    private Map map;

    public final boolean isPlayer = false;

    BufferedImage image = null;

    public String socketId = null;

    public Entity(Socket socket, String socketId, Map map, String path, int x, int y) {
        //this.level = level;
        this.registerSocketListener("playermove", this);
        this.path = path;
        entityX = x;
        entityY = y;
        this.map = map;

        //System.out.println(socketId.toString());

        this.socketId = socketId;

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

    public void onUnload() {
        
    }

    protected void moveUp() {
        System.out.println("move up");
        if (this.canWalkThere(this.entityX, this.entityY - 1)) {
            //entityY--;
            getSocket().emit("moveTo", entityX, entityY - 1);
        }
    }

    protected void moveDown() {
        //System.out.println("walk down? " + this.canWalkThere(this.entityX, this.entityY + 1));
        if (this.canWalkThere(this.entityX, this.entityY + 1)) {
            //entityY++;
            getSocket().emit("moveTo", entityX, entityY + 1);
        }
    }

    protected void moveLeft() {
        if (this.canWalkThere(this.entityX - 1, this.entityY)) {
            //entityX--;
            getSocket().emit("moveTo", entityX - 1, entityY);
        }
    }

    protected void moveRight() {
        if (this.canWalkThere(this.entityX + 1, this.entityY)) {
            //entityX++;
            getSocket().emit("moveTo", entityX + 1, entityY);
        }
    }

    @Override
    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {
        Graphics g = image.getGraphics();
        g.setColor(new Color(127, 127, 255, 127));
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();
    }

    private boolean canWalkThere(int x, int y) {
        ArrayList<Boolean> states = new ArrayList<>();

        for (int i = 0; i < map.getLayerCount(); i++) {
            if (((TileLayer) map.getLayer(i)).getTileAt(x, y) == null) {
                states.add(true);
            }
        }

        return states.size() == 0;
    }

    @Override
    public void draw(int[] pixels, BufferedImage image, int offset, int row) {
        image.setRGB(0, 0, width, height, getSprite(), 0, height);
    }

    @Override
    public void drawGraphics(Graphics2D g) {
        g.drawImage(getSpriteBuffer(), null, 0, 0 );
    }

    @Override
    public void update(double delta) {

    }

    public int[] getSprite() {
        int[] p = new int[32 * 32];
        image.getRGB(0, 0, 32, 32, p, 0, 32);

        return p;
    }

    public BufferedImage getSpriteBuffer() {
        return image.getSubimage(0, 0, 32, 32);
    }

    private void setPosition(int x, int y) {
        this.entityX = x;
        this.entityY = y;
    }

    @Override
    public void call(Object... objects) {
        String id = objects[0].toString();

        if (id.equals(this.socketId)) {
            //System.out.println("Move Update for " + id + ", this socket id: " + this.socketId);

            setPosition((int) objects[1], (int) objects[2]);
            //System.out.println(this.socketId + " is now at (" + entityX + "/" + entityY + ")");
        }
    }
}
