package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;
import com.prismflux.canvastest.net.SocketConnection;
import io.socket.emitter.Emitter;
import org.mapeditor.core.Map;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.OrthogonalRenderer;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class Screen extends SocketConnection implements Renderable, Emitter.Listener {
    public static final int MAP_WIDTH = 32;
    public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = TILE_WIDTH;
    public int width;
    public int height;
    public int tileWidth;
    public int tileHeight;

    //public Level level;

    private Map map = null;
    private OrthogonalRenderer renderer = null;

    private int clipX = 0;
    private int clipY = 0;

    //private int positionX = 0;
    //private int positionY = 0;

    private ArrayList<Entity> players = new ArrayList<>();

    public Screen getScreen() {
        return this;
    }

    public Screen(int width, int height) {
        getSocket().emit("room", "/levels/real_map2_20x20.tmx");
        this.width = width;
        this.height = height;
        this.registerSocketListener("loadlevel", this);
        this.registerSocketListener("playerjoin", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("[" + getSocket().id() + "]New Player with ID: " + objects[0] + " on map");
                System.out.println("Current Player on map " + players.size());
                //System.out.println(objects);
                if (objects[0].equals(getSocket().id())) {
                    System.out.println("Spawning Player");
                    players.add(new Player(getSocket(), map, getScreen(), (int) objects[1], (int) objects[2]));
                } else {
                    System.out.println("Spawning Entity " + objects[0]);
                    players.add(new Entity(getSocket(), (String) objects[0], map, "/entities/Base.png", (int) objects[1], (int) objects[2]));
                }
            }
        });

        this.registerSocketListener("playerleave", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("Leaving Player");

                int index = -1;

                for (int i = 0; i < players.size(); i++) {
                    Entity e = players.get(i);
                    if (e.socketId.equals(objects[0].toString())) {
                        index = i;
                    }
                }

                if (index != -1) {
                    players.get(index).onUnload();
                    players.remove(index);
                    System.out.println(objects[0] + " left");
                } else {
                    System.out.println("index not found for id " + (String) objects[0]);
                }
            }
        });

        Game.addToQueue(this);
    }

    private int getPlayerXWithOffset() {
        if (getPlayer() != null) {
            return ((Game.GAME_WIDTH / 2) - (tileWidth) - ((getPlayer().getEntityX() * 32) + getPlayer().getXOffset()));
        } else {
            return 0;
        }
    }

    private int getPlayerYWithOffset() {
        if (getPlayer() != null) {
            return ((Game.GAME_HEIGHT / 2) - (tileHeight) - (getPlayer().getEntityY() * 32 + getPlayer().getYOffset()));
        } else {
            return 0;
        }
    }

    @Override
    public void drawGraphics(Graphics2D g_) {
        Graphics2D g = (Graphics2D) g_.create();
        if (renderer != null && map != null) {

            //You have to create a seperate graphics context, because otherwise it wont draw

            for (int i = 0; i < map.getLayerCount(); i++) {
                TileLayer tileLayer = (TileLayer) map.getLayer(i);
                if (getPlayer() != null) {
                    int posX = ((Game.GAME_WIDTH / 2) - (tileLayer.getTileAt(0, 0).getWidth() / 2)) - (getPlayer().getEntityX() * 32);
                    int posY = ((Game.GAME_HEIGHT / 2) - (tileLayer.getTileAt(0, 0).getHeight() / 2)) - (getPlayer().getEntityY() * 32);
                    g.translate(posX, posY);
                }

                renderer.paintTileLayer(g, tileLayer);
            }

            drawEntitiesDebug(g);

            g.dispose();
        }
    }

    @Override
    public void update(double delta) {

    }

    public void drawDebug(Graphics2D g_) {
        //You have to create a seperate graphics context, because otherwise it wont draw
        Graphics2D g = (Graphics2D) g_.create();

        if (renderer != null && map != null) {
            for (int i = 0; i < map.getLayerCount(); i++) {
                TileLayer tileLayer = (TileLayer) map.getLayer(i);
                g.translate(getPlayerXWithOffset(), getPlayerYWithOffset());

                renderer.paintTileLayer(g, tileLayer);
            }

            drawEntitiesDebug(g);

            g.dispose();
        }
    }

    private void drawEntitiesDebug(Graphics2D g) {
        for (int i = 0; i < players.size(); i++) {
            Entity e = players.get(i);
                e.drawGraphics(g);
        }
    }

    private void drawEntities(Graphics2D g) {
        for (int i = 0; i < players.size(); i++) {
            Entity e = players.get(i);
            if (e.socketId != getSocket().id()) {
                e.drawGraphics(g);
            }
        }

        /*if (getPlayer() != null) {
            getPlayer().drawGraphics(g);
        }*/
    }

    int XYtoIndex(int x, int y) {
        return (y * this.width) + x;
    }

    private Player getPlayer() {
        Player p = null;

        for (int i = 0; i < players.size(); i++) {
            if (getSocket().id().equals(players.get(i).socketId)){
                p = (Player) players.get(i);
            }
        }

        return p;
    }

    //gets called when socket emits a loadlevel event
    @Override
    public void call(Object... objects) {
        //level = new Level((String) objects[0]);
        try {
            //System.out.println(objects[0]);

            File f = new File("./res/levels");
            map = new TMXMapReader().readMap(Screen.class.getResourceAsStream((String) objects[0]), f.getCanonicalPath());

            tileWidth = map.getTileHeightMax();
            tileHeight = map.getTileHeightMax();
            //System.out.println("Width: " + width + ", height: " + height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderer = new OrthogonalRenderer(map);
    }

    /* ANIMATION STUFF */
    /*@Override
    public void resetAnimation() {
        progress = 0;
        d = null;
        duration = -1;
        xOffset = 0;
        yOffset = 0;
    }

    @Override
    public void updateOffsets() {
//        System.out.println(getAnimationDirection());
        switch (getAnimationDirection()) {
            case UP:
                yOffset = getProgressAsPercentage() * height;
                break;
            case DOWN:
                yOffset = (getProgressAsPercentage() * -height);
                //System.out.println(getProgressAsPercentage() * height);
                break;
            case LEFT:
                xOffset = getProgressAsPercentage() * width;
                break;
            case RIGHT:
                xOffset = getProgressAsPercentage() * -width;
                break;
        }
    }

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public int getXOffset() {
        return (int) xOffset;
    }

    @Override
    public int getYOffset() {
        return (int) yOffset;
    }

    private Direction d = null;
    @Override
    public Direction getAnimationDirection() {
        return d;
    }

    @Override
    public void setAnimationDirection(Direction d_) {
        d = d_;
    }

    private int duration = -1;

    @Override
    public void setAnimationDuration(int duration_) {
        duration = duration_;
    }

    @Override
    public int getAnimationDuration() {
        return duration;
    }

    private double progress = 0;

    @Override
    public void setProgress(double deltaTick) {
        progress += deltaTick;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    private double getProgressAsPercentage() {
        return progress / duration;
    }

    @Override
    public boolean shouldAnimate() {
        return getAnimationDirection() != null;
    }*/
}
