package com.prismflux.schrottimon.gfx;

import com.prismflux.schrottimon.Game;
import com.prismflux.schrottimon.InputHandler;
import io.socket.client.Socket;
import org.mapeditor.core.Map;
import org.mapeditor.core.MapObject;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener {

    public final boolean isPlayer = true;
    private Screen parentScreen = null;

    private boolean running = false;

    public Player(Socket socket, Map map, Screen s, int x, int y) {
        super(getSocket(), getSocket().id(), map, "/entities/Character.png", x, y);
        parentScreen = s;

        InputHandler.keyListeners.add(this);
    }

    protected void moveUp() {
        if (!this.shouldAnimate() && Game.isScreenBlocked()) {
            setDirection(Direction.UP);
            getSocket().emit("moveTo", getEntityX(), getEntityY() - 1, running);
        }
    }

    protected void moveDown() {
        if (!this.shouldAnimate() && Game.isScreenBlocked()) {
            setDirection(Direction.DOWN);
            getSocket().emit("moveTo", getEntityX(), getEntityY() + 1, running);
        }
    }

    protected void moveLeft() {
        if (!this.shouldAnimate() && Game.isScreenBlocked()) {
            setDirection(Direction.LEFT);
            getSocket().emit("moveTo", getEntityX() - 1, getEntityY(), running);
        }
    }

    protected void moveRight() {
        if (!this.shouldAnimate() && Game.isScreenBlocked()) {
            setDirection(Direction.RIGHT);
            getSocket().emit("moveTo", getEntityX() + 1, getEntityY(), running);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //running = e.getKeyCode() == KeyEvent.VK_ALT;
        running = e.isShiftDown();
        //System.out.println(running + " char: " + e.getKeyChar());

        switch (Character.toLowerCase(e.getKeyChar())) {
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

            case 'z':
                MapObject o = parentScreen.getTeleportForTileUnderPlayer(getEntityX(), getEntityY());
                if (o != null) {
                    getSocket().emit("room", o.getProperties().getProperty("target"));
                }
                break;
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        InputHandler.removeKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        running = e.isShiftDown();
        /*switch (e.getKeyCode()) {
            case KeyEvent.VK_ALT:
                running = true;
                break;
        }*/
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
