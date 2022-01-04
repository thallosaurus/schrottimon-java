package com.prismflux.canvastest.gfx;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Renderable {

    void drawDebug(Graphics2D g);
    void drawGraphics(Graphics2D g);
    void update(double delta);
}
