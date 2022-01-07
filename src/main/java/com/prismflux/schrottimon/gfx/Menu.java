package com.prismflux.schrottimon.gfx;

import com.prismflux.schrottimon.Game;
import com.prismflux.schrottimon.InputHandler;
import com.prismflux.schrottimon.ui.TestComponent;
import com.prismflux.schrottimon.ui.UIComponent;

import java.awt.*;
import java.awt.event.*;

public class Menu implements Renderable, KeyListener {

    private boolean show = false;
    //private final JRootPane jrp;
    //ArrayList<UIComponent> components = new ArrayList<>();
    private UIComponent root = new UIComponent("root");

    public Menu(Game g) {
        InputHandler.keyListeners.add(this);
        Game.addToQueue(this);

        //components.add();
        //UIComponent u = new UIComponent("Root");
        root.addComponent(new TestComponent("Test"));
        root.addComponent(new TestComponent("Test2"));
        //components.add(u);
    }

    @Override
    public void drawDebug(Graphics2D g) {
        if (show) {
            root.draw(g);
            //jrp.paint(g);
            //frame.paint(g);

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

            Game.blockScreenInput(show);
        }

        if (show) {
            switch (e.getKeyChar()) {
                case 'w':
                    root.decreasePointer();
                    break;
                case 's':
                    root.increasePointer();
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
