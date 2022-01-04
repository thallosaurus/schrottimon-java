package com.prismflux.canvastest.gfx;

public interface Animatable {
    public void resetAnimation();

    public void updateOffsets();

    public int getXOffset();
    public int getYOffset();
    public Direction getAnimationDirection();
    public void setAnimationDirection(Direction d);
    public void setAnimationDuration(int duration);
    public int getAnimationDuration();

    public void setProgress(double deltaTick);
    public double getProgress();

    public boolean shouldAnimate();

    public void initAnimation(Direction d);
}
