package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;
import com.prismflux.canvastest.InputHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Menu implements Renderable, KeyListener {

    private boolean show = false;

    public Menu() {
        InputHandler.keyListeners.add(this);
        Game.addToQueue(this);
    }

    @Override
    public void drawDebug(Graphics2D g) {
        if (show) {
            g.setColor(Color.YELLOW);

            g.drawString("Hello", 50, 50);


        }
        g.dispose();
    }

    @Override
    public void drawGraphics(Graphics2D g) {
        if (show) {
            g.setColor(Color.GRAY);

            g.drawString("Hello", 50, 50);
        }

        g.dispose();
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == 'e') {
            System.out.println("e was pressed");
            show = !show;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
