package com.prismflux.schrottimon.gfx;

import com.prismflux.schrottimon.net.SocketConnection;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.mapeditor.core.Map;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Entity extends SocketConnection implements Renderable, Emitter.Listener, Animatable {

    private int entityX;
    private int entityY;
    public int width = 32;
    public int height = 32;
    private final String path;

    public Map getMap() {
        return map;
    }

    private final Map map;

    public final boolean isPlayer = false;

    BufferedImage image = null;

    public String socketId = null;

    public float alpha = 1.0f;

    protected Direction direction = Direction.DOWN;

    public Entity(Socket socket, String socketId, Map map, String path, int x, int y) {
        this.registerSocketListener("playermove", this);
        this.registerSocketListener("playerupdate", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                try {
                    JSONObject obj = new JSONObject(objects[1].toString());
                    boolean dimmed = obj.getBoolean("dimmed");
                    System.out.println("Dimmed:" + dimmed);

                    if (objects[0].equals(getSocketId())) {
                        alpha = dimmed ? 0.5f : 1.0f;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(objects[1].toString());
            }
        });
        this.path = path;

        System.out.println(x);
        System.out.println(y);
        entityX = x;
        entityY = y;
        this.map = map;
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

    public void onUnload() {
        System.out.println("Unregistering Socket Events");
        unregisterSocketListener("playermove", this);
        unregisterSocketListener("playerupdate", this);
    }

    @Override
    public void drawDebug(Graphics2D g) {
        Graphics2D g_ = (Graphics2D) g.create();
        g_.translate(getXOffset(), getYOffset());
        //g_.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g_.drawImage(getSpriteBuffer(), null, this.entityX * width, this.entityY * width);
        //g_.drawImage(getAlphaChannel(), null, this.entityX * width, this.entityY * width);
        g_.dispose();
    }

    protected void setDirection(Direction d) {
        direction = d;
    }

    private BufferedImage getAlphaChannel() {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = img.createGraphics();
        //g.setColor(Color.WHITE);
        //g.setRenderingHint(RENDERING);
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(comp);
        g.fillRect(0, 0, width, height);

        g.dispose();

        return img;
    }

    @Override
    public void drawGraphics(Graphics2D g) {
        Graphics2D g_ = (Graphics2D) g.create();
        g_.translate(getXOffset(), getYOffset());
        g_.drawImage(getSpriteBuffer(), null, this.entityX * width, this.entityY * width);
        g_.dispose();
    }

    @Override
    public void update(double delta) {

    }

    public BufferedImage getSpriteBuffer() {
        int y = 0;
        if (shouldAnimate()) {
            int subFrameDuration = duration;
            int prog = ((int) getProgress()) % subFrameDuration * 10;
            y = 1 + (prog / 100);
        }
        //return
        //BufferedImage img = image.getSubimage(this.direction.ordinal() * width, y * width, 32, 32);
        //Graphics2D g = (Graphics2D) img.getGraphics();
        //Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        //g.setComposite(comp);

        //g.dispose();
        return image.getSubimage(this.direction.ordinal() * width, y * width, 32, 32);
    }

    protected void setPosition(int x, int y) {
        this.entityX = x;
        this.entityY = y;
    }

    @Override
    public void call(Object... objects) {
        String id = objects[0].toString();

        if (id.equals(this.socketId)) {
            System.out.println("playermove Update for " + this.socketId + "(Sent ID: " + id + "), Direction: " + objects[3].toString() + ", Running: " + objects[4]);
            //System.out.println("Move Update for " + id + ", this socket id: " + this.socketId);
            setDirection(Direction.values()[Integer.parseInt(objects[3].toString())]);

            boolean dur = Boolean.parseBoolean(objects[4].toString());
            Animation.scheduleUpdate(this, Direction.values()[Integer.parseInt(objects[3].toString())], dur ? 0.25f : 0.5);

            setPosition((int) objects[1], (int) objects[2]);
            //System.out.println(this.socketId + " is now at (" + entityX + "/" + entityY + ")");
        }
    }

    //ANIMTION STUFF

    private int xOffset = 0;
    private int yOffset = 0;
    private double animationDelta = 0;
    private int duration = -1;
    private boolean shouldAnimate = false;

    private int xOffsetPixel = 0;
    private int yOffsetPixel = 0;
    private Direction animationDirection = null;

    @Override
    public void resetAnimation() {
        animationDelta = 0;
        duration = -1;
        xOffset = 0;
        yOffset = 0;
        xOffsetPixel = 0;
        yOffsetPixel = 0;
        shouldAnimate = false;
    }

    @Override
    public void updateOffsets() {
        switch (getAnimationDirection()) {
            case UP:
                yOffset = ((int) ((getProgress() / getAnimationDuration()) * height) * -1) + getyOffsetPixel();
                break;
            case DOWN:
                yOffset = ((int) ((getProgress() / getAnimationDuration()) * height)) + getyOffsetPixel();
                break;
            case LEFT:
                xOffset = ((int) ((getProgress() / getAnimationDuration()) * width) * -1) + getxOffsetPixel();
                break;
            case RIGHT:
                xOffset = ((int) ((getProgress() / getAnimationDuration()) * width)) + getxOffsetPixel();
                break;
        }
    }

    @Override
    public int getXOffset() {
        return xOffset;
    }

    @Override
    public int getYOffset() {
        return yOffset;
    }

    @Override
    public Direction getAnimationDirection() {
        return animationDirection;
    }

    @Override
    public void setAnimationDirection(Direction dir) {
        animationDirection = dir;
    }

    @Override
    public void setAnimationDuration(int d) {
        duration = d;
    }

    @Override
    public int getAnimationDuration() {
        return duration;
    }

    @Override
    public void setProgress(double deltaTick) {
        animationDelta += deltaTick;
        updateOffsets();
    }

    @Override
    public double getProgress() {
        return animationDelta;
    }

    @Override
    public boolean shouldAnimate() {
        return duration != -1;
    }

    @Override
    public void initAnimation(Direction d) {
        shouldAnimate = true;
        switch (d) {
            case DOWN:
                yOffsetPixel = -1;
                break;
            case UP:
                yOffsetPixel = 1;
                break;
            case LEFT:
                xOffsetPixel = 1;
                break;
            case RIGHT:
                xOffsetPixel = -1;
                break;
        }
    }

    public int getxOffsetPixel() {
        return xOffsetPixel * width;
    }

    public int getyOffsetPixel() {
        return yOffsetPixel * height;
    }
}
