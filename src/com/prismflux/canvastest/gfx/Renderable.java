package com.prismflux.canvastest.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Renderable {

    public void drawDebug(Graphics2D g);
    public void drawGraphics(Graphics2D g);
    public void update(double delta);
}
