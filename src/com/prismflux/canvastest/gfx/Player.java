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

    public Player(Socket socket, Map map, Screen s, int x, int y) {
        super(getSocket(), getSocket().id(), map, "/entities/Character.png", x, y);
        System.out.println("I am the player!");
        parentScreen = s;

        InputHandler.keyListeners.add(this);
    }

    protected void moveUp() {
        //System.out.println("move up");
        setDirection(Direction.UP);
        if (this.canWalkThere(getEntityX(), getEntityY() - 1)) {
            //entityY--;
            getSocket().emit("moveTo", getEntityX(), getEntityY() - 1);
        }
    }

    protected void moveDown() {
        //System.out.println("walk down? " + this.canWalkThere(this.entityX, this.entityY + 1));
        setDirection(Direction.DOWN);
        if (this.canWalkThere(getEntityX(), getEntityY() + 1)) {
            //entityY++;
            getSocket().emit("moveTo", getEntityX(), getEntityY() + 1);
        }
    }

    protected void moveLeft() {
        setDirection(Direction.LEFT);
        Animation.scheduleUpdate(this, Direction.LEFT, 20);
        if (this.canWalkThere(getEntityX() - 1, getEntityY())) {
            //entityX--;
            getSocket().emit("moveTo", getEntityX() - 1, getEntityY());
        }
    }

    protected void moveRight() {
        setDirection(Direction.RIGHT);
        if (this.canWalkThere(getEntityX() + 1, getEntityY())) {
            //entityX++;
            getSocket().emit("moveTo", getEntityX() + 1, getEntityY());
        }
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

    @Override
    public void call(Object... objects) {
        //String id = objects[0].toString();

        //if (id.equals(this.socketId)) {
        //System.out.println("Move Update for " + id + ", this socket id: " + this.socketId);
        int dir = Integer.parseInt(objects[3].toString());

        //System.out.println("Down?" + dir);
        //setDirection(Direction.values()[dir]);

        //parentScreen.setDirection(Direction.values()[Integer.parseInt(objects[3].toString())]);
        Animation.scheduleUpdate(parentScreen, direction, 64);
        //setPosition((int) objects[1], (int) objects[2]);
        //System.out.println(this.socketId + " is now at (" + entityX + "/" + entityY + ")");
        super.call(objects);
    }
}
