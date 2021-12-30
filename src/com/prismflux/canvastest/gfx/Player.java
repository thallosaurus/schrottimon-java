package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.InputHandler;

import static com.prismflux.canvastest.net.SocketConnection.getSocket;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener {

    public final boolean isPlayer = true;

    public Player(Level level, int x, int y) {
        super(getSocket(), level, "/entities/Character.png", x, y);

        InputHandler.keyListeners.add(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'a':
                //left
                moveLeft();
                break;

            case 'd':
                moveRight();
                break;

            case 'w':
                //up
                moveUp();
                break;

            case 's':
                moveDown();
                break;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
