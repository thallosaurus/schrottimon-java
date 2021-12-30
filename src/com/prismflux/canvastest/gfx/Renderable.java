package com.prismflux.canvastest.gfx;

import java.awt.image.BufferedImage;

public interface Renderable {

    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row);
    public void draw(int[] pixels, BufferedImage image, int offset, int row);
    public void update(double delta);
}
