package lt.dvim.toh;

import java.awt.Color;
import java.awt.Graphics;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Action implements IPaintable
{
    ResourceBundle messages = ResourceBundle.getBundle("toh");
    public Peg x;
    public Peg y;
    public Peg z;

    int centerX = 200;
    int centerY = 100;

    int width;
    int height;

    int vertMargin = 20;
    int horMargin = 10;

    static int minWidth = 250;

    int diskNum;

    public Action parent;

    public Action(Peg x, Peg y, Peg z, Action parent, int type, int diskNum)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.diskNum = diskNum;

        while ((width = 3 * Peg.width + horMargin*4) <= 250) {
            horMargin += 10;
        }
        height = Peg.height + vertMargin*2;

        this.parent = parent;
        if (this.parent != null) {
            centerY = parent.centerY + height;
            switch (type) {
                case 1:
                    centerX = parent.centerX - width;
                    break;
                case 2:
                    centerX = parent.centerX;
                    break;
                case 3:
                    centerX = parent.centerX + width;
                    break;
            }
        }
    }

    public void paintComponent(Graphics g)
    {
        g.setColor(Color.black);
        g.drawRect(centerX - width/2, centerY - height/2, width, height);

        String text = null;
        if (diskNum != -1) {
            text = MessageFormat.format(messages.getString("step.movement"), x.name, y.name, z.name, diskNum, getLitEndingFor(diskNum));
        }
        else {
            text = String.format(messages.getString("moving.from"), TowersOfHanoi.moveCount, x.name, y.name);
        }
        g.drawString(text, centerX - width/2 + 5, centerY - height/2 + 15);

        movePegsHere();

        x.paintComponent(g);
        y.paintComponent(g);
        z.paintComponent(g);
    }

    public void movePegsHere() {
        movePegsHorizontally(x, y, z);

        x.centerY = centerY + height/2 - vertMargin;
        z.centerY = centerY + height/2 - vertMargin;
        y.centerY = centerY + height/2 - vertMargin;
    }

    public void movePegsHorizontally(Peg a, Peg b, Peg c) {
        a.centerX = centerX - width/2 + Peg.width/2 + horMargin;
        b.centerX = centerX - width/2 + Peg.width + Peg.width/2 + horMargin*2;
        c.centerX = centerX - width/2 + Peg.width + Peg.width + Peg.width/2 + horMargin*3;
    }

    public String getLitEndingFor(int num) {
        if (num % 10 == 1 && num % 100 != 11)
            return "ą";
        else if (num % 10 == 0 || (num % 100 >= 11 && num % 100 <= 19))
            return "ų";
        else
            return "us";
    }
}
