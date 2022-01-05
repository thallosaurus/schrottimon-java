package com.prismflux.canvastest.ui;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;

public class Frame extends JFrame implements MouseInputListener {
    public Frame() {
        super("Hello");

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.dispatchEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.dispatchEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.dispatchEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        this.dispatchEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        this.dispatchEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.dispatchEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.dispatchEvent(e);
    }
}
