package com.prismflux.canvastest.gfx;

import com.google.gson.Gson;
import com.prismflux.canvastest.Game;
import com.prismflux.canvastest.json.JsonMap;
import com.prismflux.canvastest.json.TiledLayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapeditor.core.*;
import org.mapeditor.io.MapReader;
import org.mapeditor.io.TMXMapReader;
import org.mapeditor.view.IsometricRenderer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @deprecated
 */
public class Level implements Renderable, Animatable {

    public int width = 17;
    public int height = 6;

    private ArrayList<Entity> entities = new ArrayList<>();

    public String path;

    public float xOffset = 0;
    public float yOffset = 0;

    public SpriteSheet sheet = null;
    IsometricRenderer renderer = null;

    private JSONObject mapFile;
    private int[] map;

    /*private int[] map = {
            0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };*/

    //TiledMap tmap = null;

    public Level(String path) {
        super();
        //this.path = path;
        setLevel(path);

        //InputStream is = Level.class.getResourceAsStream(path);
        //is.

        //set level width/height according to tmx file
        //load spritesheet here

        //this.mapFile

        //addEntity(new Player(this, 3, 3));

        //addEntity(new Entity(getSocket(), this, "/entities/Base.png", 1, 1));
        //addEntity(new Entity(getSocket(), this, "/entities/Base.png", 1, 2));
        //addEntity(new Entity(getSocket(), this, "/entities/Base.png", 1, 3));

        Animation.animationQueue.add(this);

        //nothing to do, because tmx isnt implemented yet
    }

    private void setLevel(String levelId) {
        this.path = levelId;

        //String s = loadLevelFromFiles(levelId);
        //loadTMXFromFile(levelId);

        //this.mapFile = new JSONObject(s, );

        //Gson gson = new Gson();
        //JsonMap j = gson.fromJson(s, JsonMap.class);
        //com.prismflux.canvastest.json.TiledLayer tl = (TiledLayer) a.get(0);
        //TiledLayer tl = j.layers[0];

        //this.map = tl.data;
        //this.width = tl.width;
        //this.height = tl.height;

        //this.sheet = new SpriteSheet("/cuterpg/Tiles.png");
    }

    /*private String loadLevelFromFiles(String path) {
        InputStream is = Level.class.getResourceAsStream(path);
        return new BufferedReader(new InputStreamReader(is)).lines().parallel().collect(Collectors.joining("\n"));
    }*/

    private void loadTMXFromFile(String path) {
        Map map_ = null;
        try {
            map_ = new TMXMapReader().readMap(Level.class.getResourceAsStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {
        int pixelsWidth = row;
        int pixelsHeight = pixels.length / pixelsWidth;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((x * sheet.spriteWidth + 32) < Game.GAME_WIDTH && (y * sheet.spriteHeight + 32) < Game.GAME_HEIGHT) {
                    int index = XYtoIndex(x, y);
                    int[] sprite = sheet.getSpriteDebug(map[index]);

                /*if (getPlayerX() == x && getPlayerY() == y) {
                    for (int i = 0; i < sprite.length; i++) {
                        sprite[i] &= 0xffff00;
                    }
                }*/
                    //System.out.println((x * sheet.spriteWidth + getXOffset()) + ", " + (y * sheet.spriteHeight + getYOffset()));
                    image.setRGB(x * sheet.spriteWidth + getXOffset(), (y * sheet.spriteHeight) + getYOffset(), sheet.spriteWidth, sheet.spriteHeight, sprite, 0, sheet.spriteHeight);
                }
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

    }

    @Override
    public void drawGraphics(Graphics2D g) {

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

    private JSONArray getLayers() throws JSONException {
        return mapFile.getJSONArray("layers");
    }
}
