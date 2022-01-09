package com.prismflux.schrottimon.gfx;

import com.prismflux.schrottimon.Game;
import com.prismflux.schrottimon.InputHandler;
import com.prismflux.schrottimon.net.SocketConnection;
import io.socket.emitter.Emitter;
import org.mapeditor.core.*;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.OrthogonalRenderer;

import java.awt.*;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Screen extends SocketConnection implements Renderable, Emitter.Listener, KeyListener {
    public static final int MAP_WIDTH = 32;
    //public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = TILE_WIDTH;
    public int width;
    public int height;
    public int tileWidth;
    public int tileHeight;

    private Map map = null;
    private OrthogonalRenderer renderer = null;

    private final ArrayList<Entity> players = new ArrayList<>();

    public boolean useLightMap = false;
    private LightMap lightMap = null;

    public Screen getScreen() {
        return this;
    }

    public Screen(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.registerSocketListener("loadlevel", this);
        this.registerSocketListener("playerjoin", objects -> {
            System.out.println("New Player with ID: " + objects[0] + " on map");
            System.out.println("Current Player on map " + players.size());
            if (objects[0].equals(getSocketId())) {
                System.out.println("Spawning Player");
                System.out.println(objects[1] + ", " + objects[2]);
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

        lightMap = new LightMap(this);
        Game.addToQueue(lightMap);
        InputHandler.keyListeners.add(this);
    }

    int getPlayerXWithOffset() {
        if (getPlayer() != null) {
            return ((Game.GAME_WIDTH / 2) - (tileWidth / 2) - ((getPlayer().getEntityX() * 32) + getPlayer().getXOffset()));
        } else {
            return 0;
        }
    }

    int getPlayerYWithOffset() {
        if (getPlayer() != null) {
            return ((Game.GAME_HEIGHT / 2) - (tileHeight / 2) - (getPlayer().getEntityY() * 32 + getPlayer().getYOffset()));
        } else {
            return 0;
        }
    }

    @Override
    public void drawGraphics(Graphics2D g_) {
        //You have to create a seperate graphics context, because otherwise it wont draw
        Graphics2D g = (Graphics2D) g_.create();

        if (hasMapLoaded()) {
            drawTileLayers(g);
            drawEntities(g);
            drawObjectLayers(g);
            g.dispose();
        }
    }

    private void drawTileLayers(Graphics2D g) {
        g.translate(getPlayerXWithOffset(), getPlayerYWithOffset());

        ArrayList<TileLayer> list = getTileLayers();
        for (int i = 0; i < list.size(); i++) {
            //boolean visible = list.get(i).isVisible() != null ? false : true;
            //if (visible) {
            //if (list.get(i).isVisible()) {
            renderer.paintTileLayer(g, list.get(i));
            //}
        }
    }

    private void drawObjectLayers(Graphics2D g) {
        ArrayList<ObjectGroup> list = getObjectLayers();
        for (int i = 0; i < list.size(); i++) {
            //boolean visible = list.get(i).isVisible() != null ? false : true;
            //if (visible) {
            renderer.paintObjectGroup(g, list.get(i));
            //}
        }
    }

    @Override
    public void update(double delta) {
        if (getSocket().connected() == false && hasMapLoaded()) {
            invalidate();
        }
    }

    @Override
    public void invalidate() {
        this.map = null;
        this.renderer = null;

        for (int i = 0; i < players.size(); i++) {
            players.get(i).invalidate();
        }

        players.clear();
    }

    public void drawDebug(Graphics2D g_) {
        //You have to create a seperate graphics context, because otherwise it wont draw
        Graphics2D g = (Graphics2D) g_.create();

        if (hasMapLoaded()) {
            //drawTileLayers(g);
            //drawEntities(g);
            //drawObjectLayers(g);
            //g.dispose();

            g.translate(getPlayerXWithOffset(), getPlayerYWithOffset());

            java.util.List<MapLayer> ml = map.getLayers();
            for (int i = 0; i < map.getLayerCount(); i++) {

                if (ml.get(i) instanceof ObjectGroup) {
                    renderer.paintObjectGroup(g, (ObjectGroup) ml.get(i));
                }

                if (ml.get(i) instanceof TileLayer) {
                    renderer.paintTileLayer(g, (TileLayer) ml.get(i));

                    if ("root".equals(ml.get(i).getName())) {
                        drawEntitiesDebug(g);
                    }
                }
            }
        }

        g.dispose();
    }

    private void drawEntitiesDebug(Graphics2D g) {
        for (int i = 0; i < players.size(); i++) {
            Entity e = players.get(i);
            e.drawDebug(g);
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
            if (getSocketId().equals(players.get(i).socketId)) {
                p = (Player) players.get(i);
            }
        }

        return p;
    }

    //gets called when socket emits a loadlevel event
    @Override
    public void call(Object... objects) {
        try {
            File f = new File("./res/spritesheets");
            URL u = new URL("http://localhost:9000/data/levels/" + objects[0].toString());
            //URL assets = new URL("http://localhost:9000/data/assets");

            map = new TMXMapReader().readMap(u.openStream(), f.getCanonicalPath());

            tileWidth = map.getTileHeightMax();
            tileHeight = map.getTileHeightMax();

            lightMap.invalidate();

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

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'l') {
            useLightMap = !useLightMap;

            return;
        }

        if (e.getKeyChar() == 'i') {
            lightMap.invalidate();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /* SHADOW STUFF */
    public Point getMiddlePosition() {
        return new Point((Game.GAME_WIDTH / 2), (Game.GAME_HEIGHT / 2));
    }

    public boolean hasMapLoaded() {
        return map != null && renderer != null;
    }

    public Map getMap() {
        return map;
    }

    public ArrayList<ObjectGroup> getObjectLayers() {
        ArrayList<ObjectGroup> ogList = new ArrayList<>();

        for (int i = 0; i < map.getLayerCount(); i++) {
            MapLayer og = map.getLayer(i);
            if (og instanceof ObjectGroup) {
                ogList.add((ObjectGroup) og);
            }
        }

        return ogList;
    }

    public ArrayList<TileLayer> getTileLayers() {
        ArrayList<TileLayer> tlList = new ArrayList<>();

        for (int i = 0; i < map.getLayerCount(); i++) {
            MapLayer og = map.getLayer(i);
            if (og instanceof TileLayer) {
                tlList.add((TileLayer) og);
            }
        }

        return tlList;
    }
}
