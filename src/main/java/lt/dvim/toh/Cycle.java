package lt.dvim.toh;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ResourceBundle;

public class Cycle implements IPaintable {

    ResourceBundle messages = ResourceBundle.getBundle("toh");
    public Peg x;
    public Peg y;
    public Peg z;

    int type = 0; // even number of disks - 0, odd - 1

    int centerX = 500;
    int centerY = 100;

    public Cycle(Peg x, Peg y, Peg z, int type) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.type = type;

        movePegs();
    }

    public void movePegs() {
        x.centerX = centerX - Peg.width;
        z.centerX = centerX + Peg.width;
        y.centerX = centerX;

        x.centerY = centerY;
        z.centerY = centerY;
        y.centerY = centerY + Peg.height;
    }

    public void paintComponent(Graphics g)
    {
        g.setColor(Color.black);
        g.drawArc(centerX-Peg.width*2, centerY-Peg.height*2, Peg.width*4, Peg.height*4, -45, 270);

        String text;
        if (type == 0) { // clockwise
            // get the points by the ellipsis equation
            int arrowX = (int)Math.ceil(Peg.width*2 * Math.cos(Math.PI/4) + centerX);
            int arrowY = (int)Math.ceil(Peg.height*2 * Math.sin(Math.PI/4) + centerY);

            // lower arrow side
            g.drawLine(arrowX, arrowY, arrowX + 20, arrowY - 4);
            // upper arrow side
            g.drawLine(arrowX, arrowY, arrowX + 10, arrowY - 20);

            text = messages.getString("disk.moved.counterclockwise.direction");
        }
        else { // counter-clockwise
            // get the points by the ellipsis equation
            int arrowX = (int)Math.ceil(Peg.width*2 * Math.cos((Math.PI/4)*3) + centerX);
            int arrowY = (int)Math.ceil(Peg.height*2 * Math.sin((Math.PI/4)*3) + centerY);

            // lower arrow side
            g.drawLine(arrowX, arrowY, arrowX - 20, arrowY - 4);
            // upper arrow side
            g.drawLine(arrowX, arrowY, arrowX - 10, arrowY - 20);
            g.drawLine(arrowX, arrowY, arrowX - 10, arrowY - 20);

            text = messages.getString("disk.moved.in.clockwise.direction");
        }

        //g.drawString(text, centerX - Peg.width*2, centerY + Peg.height*2 + 10);
        TowersOfHanoi.setCaption(messages.getString("iterative.solution") +text);

        x.paintComponent(g);
        y.paintComponent(g);
        z.paintComponent(g);
    }
}
