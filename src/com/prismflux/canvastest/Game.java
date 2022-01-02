package com.prismflux.canvastest;

import com.prismflux.canvastest.gfx.*;
import com.prismflux.canvastest.net.SocketConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

public class Game extends Canvas implements Runnable, Renderable, KeyListener {

    static JFrame frame;
    public int tickCount = 0;

    public static final int GAME_WIDTH = 480; //480;
    public static final int GAME_HEIGHT = GAME_WIDTH / 12 * 9;
    public static final int SCALE = 2;
    private static ArrayList<Renderable> renderQueue = new ArrayList<>();

    private BufferedImage image = new BufferedImage(GAME_WIDTH, GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

    public static boolean debug = true;
    private static Game g;

    //private Screen screen;

    public static void main(String[] args) {
	// write your code here
        g = new Game();
        g.startEntry();
    }

    public static Game getGame() {
        return g;
    }

    public void init() {
        //screen =
        //new SocketConnection(this);
        new InputHandler(this);
        new Screen(GAME_WIDTH, GAME_HEIGHT /*new Level("/levels/unbenannt.tmx")*/);
        new Animation();
        addToQueue(this);
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

        /*for (int i = 0; i < pixels.length; i++) {
            pixels[i] = i + tickCount;
        }*/

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

        for (int i = 0; i < Game.renderQueue.size(); i++) {
            if (debug) {
                Game.renderQueue.get(i).drawDebug(pixels, image, 0, WIDTH);
            } else {
                Game.renderQueue.get(i).draw(pixels, image, 0, WIDTH);
            }
        }

        Graphics g = bs.getDrawGraphics();

        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

        g.dispose();
        bs.show();
    }

    public static void addToQueue(Renderable r) {
        renderQueue.add(r);
    }

    @Override
    public void drawDebug(int[] pixels, BufferedImage image, int offset, int row) {

    }

    @Override
    public void draw(int[] pixels, BufferedImage image, int offset, int row) {
        Graphics g = image.getGraphics();

        Font font = new Font("Serif", Font.PLAIN, 36);
        g.setFont(font);

        g.drawString("Hello", 0, 0);
        g.dispose();
    }

    @Override
    public void drawGraphics(Graphics2D g) {

    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 't') {
            this.debug = !this.debug;

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
