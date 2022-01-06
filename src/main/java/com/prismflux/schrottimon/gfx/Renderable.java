package com.prismflux.schrottimon.gfx;

import java.awt.*;

public interface Renderable {

    void drawDebug(Graphics2D g);
    void drawGraphics(Graphics2D g);
    void update(double delta);
}
