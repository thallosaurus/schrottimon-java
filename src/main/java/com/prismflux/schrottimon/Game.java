package com.prismflux.schrottimon;

import com.prismflux.schrottimon.gfx.*;
import com.prismflux.schrottimon.gfx.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Game extends Canvas implements Runnable, Renderable, KeyListener {

    static JFrame frame;
    public int tickCount = 0;

    public static final int GAME_WIDTH = 480; //480;
    public static final int GAME_HEIGHT = GAME_WIDTH / 12 * 9;
    public static final int SCALE = 2;
    private static final ArrayList<Renderable> renderQueue = new ArrayList<>();

    private final BufferedImage image = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);

    public static boolean debug = false;
    private static Game g;

    public static boolean inputScreen = false;

    private AuthenticationClient ac = new AuthenticationClient();

    public static void main(String[] args) {
        // write your code here
        //AuthenticationClient.showLoginPrompt();

        g = new Game();
        g.startEntry();
    }

    public static Game getGame() {
        return g;
    }

    public void init() {
        addToQueue(this);
        new InputHandler(this);
        new Screen(GAME_WIDTH, GAME_HEIGHT);
        new Menu(this);
        new Animation();
    }

    public static void blockScreenInput(boolean block) {
        inputScreen = block;
    }

    public static boolean isScreenBlocked() {
        return !inputScreen;
    }

    private synchronized void startEntry() {
        new Thread(this).start();
    }

    public Game() {
        frame = new JFrame("Hello Game World");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setMinimumSize(new Dimension(getGameWidth(), getGameHeight()));
        frame.setMaximumSize(new Dimension(getGameWidth(), getGameHeight()));
        frame.setPreferredSize(new Dimension(getGameWidth(), getGameHeight()));

        frame.setLayout(new BorderLayout());

        frame.add(this, BorderLayout.CENTER);
        frame.pack();

        InputHandler.keyListeners.add(this);

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private static int getGameWidth() {
        return GAME_WIDTH * SCALE;
    }

    private static int getGameHeight() {
        return GAME_HEIGHT * SCALE;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / 60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        init();

        while (!Thread.interrupted()) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            boolean shouldRender = false;

            while (delta >= 1) {
                ticks++;
                tick(delta);
                delta -= 1;
                shouldRender = true;
            }

            if (shouldRender) {
                frames++;
                render();
            }
            //System.out.println("Hello World");

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                //System.out.println(ticks + " ticks, " + frames + " frames skipped");
                frames = 0;
                ticks = 0;
            }
        }

        //close
    }

    public void tick(double delta) {
        tickCount++;
        for (int i = 0; i < Game.renderQueue.size(); i++) {
            Game.renderQueue.get(i).update(delta);
        }
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.scale(SCALE, SCALE);
        g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

        for (int i = 0; i < Game.renderQueue.size(); i++) {
            if (debug) {
                Game.renderQueue.get(i).drawDebug(g);
            } else {
                Game.renderQueue.get(i).drawGraphics(g);
            }
        }

        g.drawImage(image, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT * SCALE, null);

        g.dispose();
        bs.show();
    }

    public static void addToQueue(Renderable r) {
        renderQueue.add(r);
    }

    @Override
    public void drawDebug(Graphics2D g) {

        g.setColor(Color.GRAY);
        g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

    }

    @Override
    public void drawGraphics(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.setClip(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);

    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 't') {
            debug = !debug;

            System.out.println("Debug is now " + (debug ? "on" : "off"));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
