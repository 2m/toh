import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Stack;

import java.security.*;

public class Peg implements IPaintable
{
    public static final int DISK_WIDTH_DELTA = 15;
    public static final int DISK_HEIGHT = 10;

    public static final int TOP_MARGIN = 10;
    public static final int SIDES_MARGIN = 20;

    public Stack<Integer> disks;
    public String name;

    public int centerX = 200;
    public int centerY = 200;
    public static int height;
    public static int width;

    public int maxDisks;

    public Peg(String name, int maxDisks, int initDiskCount)
    {
        this.name = name;
        this.maxDisks = maxDisks;

        disks = new Stack<Integer>();
        for (int i = 1; i <= initDiskCount; i++) {
            disks.push(i);
        }

        width = maxDisks * DISK_WIDTH_DELTA + SIDES_MARGIN;
        height = maxDisks * DISK_HEIGHT + TOP_MARGIN;
    }

    public void paintComponent(Graphics g)
    {
        // base
        g.setColor(Color.black);
        g.drawLine(centerX - width/2, centerY, centerX + width/2, centerY);
        g.drawLine(centerX, centerY, centerX, centerY - height);
        g.drawString(name, centerX - 5, centerY + 15);

        for (int i = 0; i < disks.size(); i++) {
            try {
                int diskNo = getDiskNo(i);
                int diskWidth = DISK_WIDTH_DELTA * diskNo;
                int x = centerX - diskWidth/2;
                int y = centerY - DISK_HEIGHT * (i + 1);

                g.setColor(getColorForDisk(disks.elementAt(i)));
                g.fillRect(x, y, diskWidth, DISK_HEIGHT);

                g.setColor(Color.black);
                g.drawString(String.valueOf(diskNo), x, y + DISK_HEIGHT - 1);
            }
            catch (ArrayIndexOutOfBoundsException ex) {
            }
        }
    }

    public Integer getTopDiskNo() {
        return maxDisks - (disks.peek() - 1);
    }

    public Integer getDiskNo(int i) {
        return maxDisks - (disks.elementAt(i) - 1);
    }

    public Color getColorForDisk(int num)
    {
        try {
            byte[] bytesOfMessage = String.valueOf(num).getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] d = md.digest(bytesOfMessage);
            return new Color((int)d[0] + 128, (int)d[1] + 128, (int)d[2] + 128);
        }
        catch (Exception ex) {
            return null;
        }
    }
}