package com.prismflux.schrottimon.gfx;

import com.prismflux.schrottimon.Game;

import java.awt.*;
import java.util.ArrayList;

public class Animation implements Renderable, Runnable {

    public static ArrayList<Animatable> animationQueue = new ArrayList<>();
    private final int currentTS = 0;

    public Animation() {
        Game.addToQueue(this);

        new Thread(this).start();
    }

    private static boolean isAlreadyInQueue(Animatable a) {
        return animationQueue.contains(a);
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
        }

        for (int i = 0; i < animationQueue.size(); i++) {
            Animatable a = animationQueue.get(i);
            //a.initAnimation()
            a.setProgress(delta);

            if (a.getProgress() > (double) a.getAnimationDuration()) {
                a.resetAnimation();

                animationQueue.remove(a);
            } else {
                a.updateOffsets();
            }
        }
    }

    public static void scheduleUpdate(Animatable a, Direction d, double durationInSeconds) {
        if (!isAlreadyInQueue(a)) {
            a.setAnimationDirection(d);

            a.setAnimationDuration((int) (durationInSeconds * 60));
            a.initAnimation(d);
            animationQueue.add(a);
        }
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
