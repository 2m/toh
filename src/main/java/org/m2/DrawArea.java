package org.m2;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.Stack;

public class DrawArea extends Canvas {

    public Stack<IPaintable> actions = null;

    public ZoomAndPanListener zapl = null;

    public Image offScreen = null;
    public String caption = null;

    public DrawArea(Stack<IPaintable> a) {
        actions = a;

        zapl = new ZoomAndPanListener(this);
        this.addMouseWheelListener(zapl);
        this.addMouseListener(zapl);
        this.addMouseMotionListener(zapl);

        this.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent e) {
                TowersOfHanoi.doStep = true;
            }

            public void keyPressed(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        Dimension d = getSize();
        offScreen = createImage(d.width, d.height);
    }

    /**
     * Responsible for clearing the screen.
     * Will overwrite its functionality
     */
    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics gr) {
        clearOffscreenAndReturn();

        Graphics2D g = clearOffscreenAndReturn();
        g.setTransform(zapl.getCoordTransform());

        for (int i = 0; i < actions.size(); i++) {
            try {
                actions.elementAt(i).paintComponent(g);
            }
            catch (ArrayIndexOutOfBoundsException ex) {
            }
        }

        gr.drawImage(offScreen, 0, 0, null);
    }

    public Graphics2D clearOffscreenAndReturn() {
        Dimension d = getSize();
        Graphics offG = getOffScreenGraphics();
        offG.setColor(getBackground());
        offG.fillRect(0, 0, d.width, d.height);

        return (Graphics2D)offG;
    }

    public Graphics getOffScreenGraphics() {
        Dimension d = getSize();
        if (offScreen == null || offScreen.getWidth(null) != d.width || offScreen.getHeight(null) != d.height) {
            offScreen = createImage(d.width, d.height);
        }
        return offScreen.getGraphics();
    }

    public void setSource(Stack<IPaintable> a) {
        actions = a;
    }
}
