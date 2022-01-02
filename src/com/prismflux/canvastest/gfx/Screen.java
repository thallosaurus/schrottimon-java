package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;
import com.prismflux.canvastest.InputHandler;
import com.prismflux.canvastest.net.SocketConnection;
import io.socket.emitter.Emitter;
import org.mapeditor.core.Map;
import org.mapeditor.core.TileLayer;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.IsometricRenderer;
import org.mapeditor.view.OrthogonalRenderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Screen extends SocketConnection implements Renderable, Emitter.Listener, KeyListener {
    public static final int MAP_WIDTH = 32;
    public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = TILE_WIDTH;
    public int width;
    public int height;

    public Level level;

    private Map map = null;
    private OrthogonalRenderer renderer = null;

    private int clipX = 0;
    private int clipY = 0;

    private int positionX = 0;
    private int positionY = 0;

    private ArrayList<Entity> players = new ArrayList<>();

    public Screen(int width, int height) {
        getSocket().emit("room", "/levels/real_map2_20x20.tmx");
        this.width = width;
        this.height = height;
        this.registerSocketListener("loadlevel", this);
        this.registerSocketListener("playerjoin", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                System.out.println("[" + getSocket().id() + "]New Player with ID: " + objects[0] + " on map");
                //System.out.println(objects);
                if (objects[0].equals(getSocket().id())) {
                    System.out.println("Spawning Player");
                    players.add(new Player(getSocket(), map, (int) objects[1], (int) objects[2]));
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
        //InputHandler.keyListeners.add(this);
    }

    public void draw(int[] pixels, BufferedImage image, int offset, int row) {
        if (renderer != null) {
            Graphics2D g = (Graphics2D) image.getGraphics();
            drawBackground(g);

            if (getPlayer() != null) {
                g.setClip(getPlayer().getEntityX() * 32, getPlayer().getEntityY() * 32, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            }

            for (int i = 0; i < map.getLayerCount(); i++) {
                TileLayer tileLayer = (TileLayer) map.getLayer(i);
                renderer.paintTileLayer(g, tileLayer);
            }

            drawEntities(image);

            g.dispose();
        }


    }

    @Override
    public void drawGraphics(Graphics2D g) {

    }

    double d = 0;

    @Override
    public void update(double delta) {

    }

    public void drawBackground(Graphics2D g) {
        g.setColor(Color.GRAY);
        g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
    }

    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {
        if (renderer != null && map != null) {
            Graphics2D g = (Graphics2D) image.getGraphics();
            drawBackground(g);

            for (int i = 0; i < map.getLayerCount(); i++) {
                TileLayer tileLayer = (TileLayer) map.getLayer(i);
                if (getPlayer() != null) {
                    int posX = ((Game.GAME_WIDTH / 2) - (tileLayer.getTileAt(0, 0).getWidth() / 2)) - (getPlayer().getEntityX() * 32);
                    int posY = ((Game.GAME_HEIGHT / 2) - (tileLayer.getTileAt(0, 0).getHeight() / 2)) - (getPlayer().getEntityY() * 32);
                    g.translate(posX, posY);
                }

                renderer.paintTileLayer(g, tileLayer);
            }

            drawEntitiesDebug(image);

            g.dispose();
        }
    }

    private void drawEntitiesDebug(BufferedImage image) {
        for (int i = 0; i < players.size(); i++) {
            Entity e = players.get(i);
            if (e.socketId != getSocket().id()) {
                int posX = ((Game.GAME_WIDTH / 2) - (e.width / 2));
                int posY = ((Game.GAME_HEIGHT / 2) - (e.height / 2));
                //BufferedImage img = image.getSubimage(e.getXOffset()  + posX, e.getYOffset() + posY, e.width, e.height);
                Graphics2D g = (Graphics2D) image.getGraphics();

                //int[] entPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

                //e.drawDebug(entPixels, img, 0, width);
                e.drawGraphics(g);

                //image.setRGB(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height, entPixels, 0, e.height);
                //img.flush();
            }
        }

        if (getPlayer() != null) {
            int posX = (Game.GAME_WIDTH / 2) - (getPlayer().width / 2);
            int posY = (Game.GAME_HEIGHT / 2) - (getPlayer().height / 2);
            BufferedImage img = image.getSubimage(posX, posY, getPlayer().width, getPlayer().height);
            Graphics2D g = (Graphics2D) img.getGraphics();
            //int[] entPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
            //getPlayer().draw(entPixels, img, 0, width);
            getPlayer().drawGraphics(g);
        }
    }

    private void drawEntities(BufferedImage image) {
        for (int i = 0; i < players.size(); i++) {
            Entity e = players.get(i);
            BufferedImage img = image.getSubimage(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height);
            int[] entPixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();

            e.draw(entPixels, img, 0, width);

            //image.setRGB(e.getEntityX() * e.width, e.getEntityY() * e.height, e.width, e.height, entPixels, 0, e.height);
            img.flush();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        renderer = new OrthogonalRenderer(map);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'w':
                positionY++;
                break;
            case 'a':
                positionX++;
                break;
            case 's':
                positionY--;
                break;
            case 'd':
                positionX--;
                break;
            case 'u':
                getSocket().emit("room", "/levels/testmap.tmx");
                //getSocket().emit("room", "/levels/real_map2_20x20.tmx");
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
