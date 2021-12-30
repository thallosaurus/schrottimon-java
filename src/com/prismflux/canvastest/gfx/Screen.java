package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Screen implements Renderable {
    public static final int MAP_WIDTH = 32;
    public static final int MAP_WIDTH_MASK = MAP_WIDTH - 1;

    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = TILE_WIDTH;
    public int width;
    public int height;

    //public SpriteSheet sheet;
    public Level level;

    public int[] tiles = new int[MAP_WIDTH * MAP_WIDTH];

    //private static final int[] message = []
    //public int[] colors = new int[]

    public Screen(int width, int height, Level level) {
        this.width = width;
        this.height = height;
        //this.sheet = sheet;
        this.level = level;
        Game.addToQueue(this);
    }

    public void draw(int[] pixels, BufferedImage image, int offset, int row) {
        level.draw(pixels, image, offset, row);
    }

    @Override
    public void update(double delta) {

    }

    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {
        level.drawDebug(pixels, image, offset, row);
    }

    int XYtoIndex(int x, int y) {
        return (y * this.width) + x;
    }
}
