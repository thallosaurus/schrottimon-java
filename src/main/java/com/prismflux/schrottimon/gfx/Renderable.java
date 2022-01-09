package com.prismflux.schrottimon.gfx;

import java.awt.*;

public interface Renderable extends Updatable {

    void drawDebug(Graphics2D g);
    void drawGraphics(Graphics2D g);
}
