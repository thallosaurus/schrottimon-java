package com.prismflux.schrottimon.gfx;

import org.mapeditor.core.*;

import java.awt.*;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class LightMap implements Renderable {

    private Screen source;

    private static BufferedImage shadow = null;

    private BufferedImage getBackground() {
        if (shadow != null) {
            return shadow;
        }

        shadow = createEmptyShadow();
        drawLights();
        return shadow;
    }

    public LightMap(Screen source_) {
        source = source_;
    }

    private BufferedImage createEmptyShadow() {
        return new BufferedImage(source.width, source.height, BufferedImage.TYPE_INT_ARGB);
    }

    public Screen getSource() {
        return source;
    }

    public boolean display() {
        return getSource().useLightMap && mapHasLights();
    }

    @Override
    public void drawDebug(Graphics2D g) {
        if (display()) {
            p(g);
        }
    }

    @Override
    public void drawGraphics(Graphics2D g) {
        if (display()) {
            p(g);
        }
    }

    @Override
    public void update(double delta) {
        //System.out.println("update");
    }

    private static BufferedImage createExampleImage(int w, int h)
    {
        BufferedImage image = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        Random random = new Random(0);
        for (int i=0; i<200; i++)
        {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            Color c = new Color(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255));
            g.setColor(c);
            g.fillOval(x-20, y-20, 40, 40);
        }
        g.dispose();
        return image;
    }


    protected void p(Graphics2D gr)
    {
        //super.paintComponent(gr);
        Graphics2D g = (Graphics2D)gr;
        //g.drawImage(image, 0,0,null);

        //drawLights();

        invalidate();

        g.drawImage(getBackground(), 0,0, null);
    }

    private void drawLights()
    {

        Graphics2D g = getBackground().createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.setColor(new Color(0,0,16,255));
        g.fillRect(0,0,source.width,source.height);
        drawLight(g, source.getMiddlePosition(), 50);

        g.translate(source.getPlayerXWithOffset(), source.getPlayerYWithOffset());
        if (mapHasLights()) {
            ObjectGroupData og = getLightsGroup();
            java.util.List moList = og.getObjects();
            //while (og.iterator().hasNext()) {
            for (int i = 0; i < moList.size(); i++) {
                MapObject mo = (MapObject) moList.get(i);
                Point2D p = new Point2D.Double(mo.getX(), mo.getY());

                String prop = mo.getProperties().getProperty("radius");
                float radius = 1.0f;

                if (prop != null) {
                    try {
                        radius = Float.parseFloat(prop);
                    } catch (NumberFormatException nfe) {
                        radius = 50;
                    }
                } else {
                    radius = 50;
                }

                drawLight(g, p, radius);
            }
        }

        g.dispose();
    }

    private void drawLight(Graphics2D g, Point2D pt, float radius)
    {
        //float radius = 100;
        g.setComposite(AlphaComposite.DstOut);
        Point2D center = new Point2D.Double(pt.getX(), pt.getY());
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {new Color(255,255,255,255), new Color(0,0,0,0) };
        RadialGradientPaint p =
                new RadialGradientPaint(
                        center, radius, dist, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);
        g.setPaint(p);
        g.fillOval((int)(pt.getX()-radius),(int) (pt.getY()-(int)radius),(int)radius*2,(int)radius*2);
    }

    public void invalidate() {
        shadow = null;
    }

    public boolean mapHasLights() {
        return getLightsGroup() != null;
    }

    public ObjectGroup getLightsGroup() {
        if (!getSource().hasMapLoaded()) return null;

        ArrayList<ObjectGroup> ogList = getSource().getObjectLayers();
        ObjectGroup flag = null;

        for (int i = 0; i < ogList.size(); i++) {
            //System.out.println(ogList.get(i).getName());
            if ("lights".equals(ogList.get(i).getName())) {
                flag = ogList.get(i);
            }

            if (flag != null) break;
        }

        return flag;
    }

    public TileLayer getRootLayer() {
        if (!getSource().hasMapLoaded()) return null;

        //find root layer
        TileLayer flag = null;
        ArrayList<TileLayer> td = getSource().getTileLayers();

        for (int i = 0; i < td.size(); i++) {
            if ("root".equals(td.get(i).getName())) {
                //TileLayerData tl = (TileLayerData) td.get(i);
                flag = td.get(i);
            }

            if (flag != null) break;
        }

        return flag;
    }
}
