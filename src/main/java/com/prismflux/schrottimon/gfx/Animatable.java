package com.prismflux.schrottimon.gfx;

public interface Animatable {
    void resetAnimation();

    void updateOffsets();

    int getXOffset();
    int getYOffset();
    Direction getAnimationDirection();
    void setAnimationDirection(Direction d);
    void setAnimationDuration(int duration);
    int getAnimationDuration();

    void setProgress(double deltaTick);
    double getProgress();

    boolean shouldAnimate();

    void initAnimation(Direction d);
}
