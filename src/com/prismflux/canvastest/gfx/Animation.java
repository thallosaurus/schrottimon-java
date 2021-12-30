package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;

import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation implements Renderable, Runnable {

    public static ArrayList<Animatable> animationQueue = new ArrayList<>();
    private int currentTS = 0;

    public Animation() {
        Game.addToQueue(this);

        new Thread(this).start();
    }

    @Override
    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {

    }

    @Override
    public void draw(int[] pixels, BufferedImage image, int offset, int row) {

    }

    @Override
    public void update(double delta) {
        /*float off = Math.abs((float) Math.sin(delta));
        for (int i = 0; i < animationQueue.size(); i++) {
            animationQueue.get(i).setXOffset(off);
            animationQueue.get(i).setYOffset(off);
        }*/
    }

    @Override
    public void run() {
        /*while (!Thread.interrupted()) {
            float x = Math.sin(currentTS);

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < animationQueue.size(); i++) {
                animationQueue.get(i).setXOffset(x);
                animationQueue.get(i).setYOffset( x);
            }
        }*/
    }
}
