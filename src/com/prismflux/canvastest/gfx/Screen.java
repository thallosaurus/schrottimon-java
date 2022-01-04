package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;
import com.prismflux.canvastest.net.SocketConnection;
import io.socket.emitter.Emitter;
import org.mapeditor.core.*;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.OrthogonalRenderer;

import java.awt.*;
import java.io.File;
import java.net.URL;
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

    private Map map = null;
    private OrthogonalRenderer renderer = null;

    private final ArrayList<Entity> players = new ArrayList<>();

    public Screen getScreen() {
        return this;
    }

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        this.registerSocketListener("loadlevel", this);
        this.registerSocketListener("playerjoin", objects -> {
            System.out.println("[" + getSocket().id() + "]New Player with ID: " + objects[0] + " on map");
            System.out.println("Current Player on map " + players.size());
            if (objects[0].equals(getSocket().id())) {
                System.out.println("Spawning Player");
                players.add(new Player(getSocket(), map, getScreen(), (int) objects[1], (int) objects[2]));
            } else {
                System.out.println("Spawning Entity " + objects[0]);
                players.add(new Entity(getSocket(), (String) objects[0], map, "/entities/Base.png", (int) objects[1], (int) objects[2]));
            }
        });

        this.registerSocketListener("playerleave", objects -> {
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
                System.out.println("index not found for id " + objects[0]);
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

    private void drawTileLayers(Graphics2D g) {
        g.translate(getPlayerXWithOffset(), getPlayerYWithOffset());
        for (int i = 0; i < map.getLayerCount(); i++) {

            try {
                TileLayer tileLayer = (TileLayer) map.getLayer(i);
                renderer.paintTileLayer(g, tileLayer);
            } catch (ClassCastException cce) {

            }
        }
    }

    private void drawObjectLayers(Graphics2D g) {
        for (int i = 0; i < map.getLayerCount(); i++) {
            try {
                ObjectGroup og = (ObjectGroup) map.getLayer(i);
                renderer.paintObjectGroup(g, og);
            } catch (ClassCastException cce) {

            }
        }
    }

    @Override
    public void update(double delta) {

    }

    public void drawDebug(Graphics2D g_) {
        //You have to create a seperate graphics context, because otherwise it wont draw
        Graphics2D g = (Graphics2D) g_.create();

        if (renderer != null && map != null) {
            drawTileLayers(g);
            drawEntities(g);
            drawObjectLayers(g);
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
            e.drawGraphics(g);
        }
    }

    private Player getPlayer() {
        Player p = null;

        for (int i = 0; i < players.size(); i++) {
            if (getSocket().id().equals(players.get(i).socketId)) {
                p = (Player) players.get(i);
            }
        }

        return p;
    }

    //gets called when socket emits a loadlevel event
    @Override
    public void call(Object... objects) {
        try {
            File f = new File("./res/levels");
            URL u = new URL("http://localhost:9000/data/levels/" + objects[0].toString());

            map = new TMXMapReader().readMap(u.openStream(), f.getCanonicalPath());

            tileWidth = map.getTileHeightMax();
            tileHeight = map.getTileHeightMax();
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderer = new OrthogonalRenderer(map);
    }

    public MapObject getTeleportForTileUnderPlayer(int x, int y) {
        ObjectGroup warps = returnWarpLayer();
        //System.out.println(warps != null ? "warp layer found" : "warp layer not found");
        MapObject mo = null;
        if (warps != null) {
            mo = warps.getObjectNear(x * tileWidth, y * tileHeight, 4);
            //System.out.println(mo.getProperties().getProperty("target"));
        }

        return mo;
    }

    private ObjectGroup returnWarpLayer() {
        ObjectGroup og_ = null;
        for (int i = 0; i < map.getLayerCount(); i++) {
            try {
                ObjectGroup objectGroup = (ObjectGroup) map.getLayer(i);
                if (objectGroup.getName().equals("warp")) {
                    og_ = objectGroup;
                    //System.out.println(objectGroup.getName());
                    break;
                }
            } catch (ClassCastException cce) {

            }
        }

        //System.out.println(og_.getName());

        return og_;
    }
}
