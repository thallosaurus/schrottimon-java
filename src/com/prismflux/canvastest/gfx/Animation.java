package com.prismflux.canvastest.gfx;

import com.prismflux.canvastest.Game;

import java.awt.*;
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
    public void drawDebug(Graphics2D g) {

    }

    /*@Override
    public void draw(int[] pixels, BufferedImage image, int offset, int row) {

    }*/

    @Override
    public void drawGraphics(Graphics2D g) {

    }

    double d = 0;
    @Override
    public void update(double delta) {
        d += delta;

        if (d > 60) {
            d = 0;
            //System.out.println("Tick");
        }

        for (int i = 0; i < animationQueue.size(); i++) {
            Animatable a = animationQueue.get(i);
            a.setProgress(delta);

            if (a.getProgress() > (double) a.getAnimationDuration()) {
                a.resetAnimation();

                int index = animationQueue.indexOf(a);
                animationQueue.remove(index);
            } else {
                a.updateOffsets();
            }
            //animationQueue.get(i).setXOffset(x);
            //animationQueue.get(i).setYOffset(x);

        }

        /*float off = Math.abs((float) Math.sin(delta));
        for (int i = 0; i < animationQueue.size(); i++) {
            animationQueue.get(i).setXOffset(off);
            animationQueue.get(i).setYOffset(off);
        }*/
    }

    public static void scheduleUpdate(Animatable a, Direction d, int duration) {
        a.setAnimationDirection(d);

        //a.setAnimationDuration(duration);
        animationQueue.add(a);
        System.out.println("Did it call the screen? " + animationQueue.size());
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < animationQueue.size(); i++) {
                //animationQueue.get(i).setXOffset(x);
                //animationQueue.get(i).setYOffset(x);
            }
        }
    }
}
