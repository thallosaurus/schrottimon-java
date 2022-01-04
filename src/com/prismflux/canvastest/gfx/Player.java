package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.InputHandler;
import io.socket.client.Socket;
import org.mapeditor.core.Map;

import static com.prismflux.canvastest.net.SocketConnection.getSocket;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player extends Entity implements KeyListener {

    public final boolean isPlayer = true;
    private Screen parentScreen = null;

    private boolean running = false;

    public Player(Socket socket, Map map, Screen s, int x, int y) {
        super(getSocket(), getSocket().id(), map, "/entities/Character.png", x, y);
        //System.out.println("I am the player!");
        parentScreen = s;

        InputHandler.keyListeners.add(this);
    }

    protected void moveUp() {
        //System.out.println("move up");
        setDirection(Direction.UP);
        if (this.canWalkThere(getEntityX(), getEntityY() - 1)) {
            //entityY--;
            getSocket().emit("moveTo", getEntityX(), getEntityY() - 1, running);
        }
    }

    protected void moveDown() {
        //System.out.println("walk down? " + this.canWalkThere(this.entityX, this.entityY + 1));
        setDirection(Direction.DOWN);
        //if (this.canWalkThere(getEntityX(), getEntityY() + 1)) {
            //entityY++;
            getSocket().emit("moveTo", getEntityX(), getEntityY() + 1, running);
        //}
    }

    protected void moveLeft() {
        setDirection(Direction.LEFT);

        //if (this.canWalkThere(getEntityX() - 1, getEntityY())) {
            getSocket().emit("moveTo", getEntityX() - 1, getEntityY(), running);
        //}
    }

    protected void moveRight() {
        setDirection(Direction.RIGHT);
        //if (this.canWalkThere(getEntityX() + 1, getEntityY())) {
            //entityX++;
            getSocket().emit("moveTo", getEntityX() + 1, getEntityY(), running);
        //}
    }

    /*@Override
    public void moveLeft() {
        if (parentScreen != null) {
            System.out.println("parentscreen != null");
            Animation.scheduleUpdate(parentScreen, Direction.RIGHT, 250);
        }
        //super.moveLeft();
    }*/

    @Override
    public void keyTyped(KeyEvent e) {
        running = false;
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                running = true;
        }

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
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
                running = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
