package com.prismflux.schrottimon.gfx;

import com.prismflux.schrottimon.Game;
import com.prismflux.schrottimon.InputHandler;
import com.prismflux.schrottimon.ui.TestComponent;
import com.prismflux.schrottimon.ui.UIComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Menu extends JFrame implements Renderable, KeyListener, MouseListener, MouseMotionListener {

    //JPanel panel = new JPanel();

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

        g.addMouseMotionListener(this);
        g.addMouseListener(this);

        //panel.addMouseListener(this);
        //panel.addMouseMotionListener(this);
        add(new JButton("Hello World"));

        resize(new Dimension(Game.GAME_WIDTH, Game.GAME_HEIGHT));

        setMinimumSize(new Dimension(Game.GAME_WIDTH, Game.GAME_HEIGHT));
        setMaximumSize(new Dimension(Game.GAME_WIDTH, Game.GAME_HEIGHT));
        setPreferredSize(new Dimension(Game.GAME_WIDTH, Game.GAME_HEIGHT));
        //this.set

        setVisible(true);

        System.out.println(getWidth() + ", " + getHeight());

        //panel.pain
        //components.add(u);
    }

    @Override
    public void drawDebug(Graphics2D g) {
        if (show) {
            //root.draw(g);
            //container.update((Graphics) g);
            //super.paint(g);
            getContentPane().paintComponents(g);
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
        if (show) {
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
        System.out.println("Click");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (show) {
            dispatchEvent(e);
        }
    }
}
