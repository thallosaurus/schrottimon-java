package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.InputHandler;
import io.socket.client.Socket;
import org.mapeditor.core.Map;

import static com.prismflux.canvastest.net.SocketConnection.getSocket;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener {

    public final boolean isPlayer = true;

    public Player(Socket socket, Map map, int x, int y) {
        super(getSocket(), getSocket().id(), map, "/entities/Character.png", x, y);
        System.out.println("I am the player!");

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
            case 'u':
                getSocket().emit("room", "/levels/unbenannt.tmx");
                break;
        }
    }

    @Override
    public void onUnload() {
        InputHandler.removeKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


}
