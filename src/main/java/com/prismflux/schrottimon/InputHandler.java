package com.prismflux.schrottimon;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class InputHandler implements KeyListener {
    public static ArrayList<KeyListener> keyListeners = new ArrayList<>();

    public InputHandler(Game gameInstance) {
        gameInstance.addKeyListener(this);
    }

    public static void removeKeyListener(KeyListener l) {
        keyListeners.remove(l);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        for (int i = 0; i < keyListeners.size(); i++) {
            keyListeners.get(i).keyTyped(e);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for (int i = 0; i < keyListeners.size(); i++) {
            keyListeners.get(i).keyPressed(e);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for (int i = 0; i < keyListeners.size(); i++) {
            keyListeners.get(i).keyReleased(e);
        }
    }
}
