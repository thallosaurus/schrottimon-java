package com.prismflux.schrottimon.ui;

import java.awt.*;
import java.util.ArrayList;

public class UIComponent {

    ArrayList<UIComponent> components = new ArrayList<>();
    private boolean chosen = false;
    private UIComponent parentComponent = null;

    private int currentSubElement = 0;

    private String name;

    public UIComponent(String n) {
        name = n;

    }

    public void setParentComponent(UIComponent parentComponent) {
        this.parentComponent = parentComponent;
    }

    public void addComponent(UIComponent u) {
        components.add(u);
        u.setParentComponent(this);
    }

    public boolean hasParent() {
        return false;
    }

    public void setChosen(boolean c) {
        chosen = c;
    }

    public boolean getChosen() {
        //return chosen;
        return parentComponent == null ? false : parentComponent.components.indexOf(this) == currentSubElement;
    }

    public String getName() {
        return name;
    }

    public void draw(Graphics2D g_) {
        Graphics2D g = (Graphics2D) g_.create();

        final int startX = 50;
        final int startY = 50;
        final int menuWidth = 150;
        final int menuHeight = 150;
        final int FONT_SIZE = 12;

        //g.setClip(startX, startY, menuWidth, menuHeight);

        g.setColor(Color.BLACK);

        g.fillRect(startX, startY, menuWidth, menuHeight);


        for (int i = 0; i < components.size(); i++) {
            Graphics2D g_Copy = (Graphics2D) g.create();
            g_Copy.translate(startX, startY + FONT_SIZE * (i + 1));
            components.get(i).drawTitle(g_Copy);
            g_Copy.dispose();
        }
        g.dispose();
    }

    public void drawTitle(Graphics2D g) {
        //if (getChosen()) {
        g.setColor(Color.YELLOW);
        //g.fillOval(0, 0, 6, 6);

        if (getChosen()) {
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
        }

        g.drawString(this.getName(), 0, 0);
    }

    public void increasePointer() {
        currentSubElement++;
        if (currentSubElement > components.size()) {
            currentSubElement = components.size();
        }
    }

    public void decreasePointer() {
        currentSubElement--;
        if (currentSubElement < 0) {
            currentSubElement = 0;
        }
    }
}
