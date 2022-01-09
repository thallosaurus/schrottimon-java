package com.prismflux.schrottimon.audio;

import com.prismflux.schrottimon.gfx.Updatable;

public class AudioPlayer implements Runnable, Updatable {

    public AudioPlayer() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        //do something
        while (!Thread.interrupted()) {

        }
    }

    @Override
    public void update(double delta) {

    }
}
