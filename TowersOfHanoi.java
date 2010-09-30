import java.awt.Color;
import java.awt.Container;
import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.event.*;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Date;

public class TowersOfHanoi extends JPanel
{
    public static int SLEEP_BETWEEN_STEPS = 2000;

    public static int diskCount = 3;

    public static Peg x = null;
    public static Peg y = null;
    public static Peg z = null;

    public static int moveCount = 0;
    public static DrawArea da;

    public static JFrame frame = null;
    public static JButton step = null;
    public static JButton cont = null;
    public static JLabel done = null;
    public static JTextArea logArea = null;
    public static JCheckBox logAreaCheck = null;
    public static JLabel caption = null;

    public static boolean doStep = false;
    public static boolean doContinue = false;
    public static boolean doReset = false;
    public static boolean solved = false;

    public static ISolution currentSolution = null;

    public static int currentSolutionType = 0;

    public static Date startedAt = null;

    public static boolean repaintAndWait() {
        try {
            da.repaint();
        }
        catch (Exception ex) {
        }

        if (solved) {
            step.setEnabled(false);
            cont.setEnabled(false);
            done.setVisible(true);
        }

        try {
            if (doContinue) {
                Thread.sleep(SLEEP_BETWEEN_STEPS);
            }

            while (!doStep && !doContinue && !doReset) {
                Thread.sleep(100);
            }
            doStep = false;
            //System.out.println(String.format("Exited doStep sleep loop. solved:%b", solved));

            // wait when it is solved
            while (solved && !doReset) {
                Thread.sleep(100);
            }

            if (doReset) {
                return false;
            }

        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        return true;
    }

    public static void addComponentsToPane(Container pane) {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        JPanel p = new JPanel();
        caption = new JLabel("Caption is right here");
        caption.setFont(caption.getFont().deriveFont(Font.BOLD, 16));
        p.add(caption);
        pane.add(p, BorderLayout.PAGE_START);

        da = new DrawArea(null);
        pane.add(da, BorderLayout.CENTER);

        p = new JPanel();
        p.setLayout(new BorderLayout());
        logArea = new JTextArea("Atlikti veiksmai:\n");
        logArea.setFont(logArea.getFont().deriveFont(Font.PLAIN, 12));
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        p.add(scrollPane, BorderLayout.CENTER);
        logAreaCheck = new JCheckBox("Registruoti atliktus veiksmus.", true);
        p.add(logAreaCheck, BorderLayout.PAGE_END);
        pane.add(p, BorderLayout.WEST);

        p = new JPanel();
        // this is needed because combo box selections are not displayed over the draw area sometimes
        p.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        step = new JButton("Vienas þingsnis");
        step.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                doStep = true;
            }
        });
        p.add(step);

        final JTextField interval = new JTextField("2", 3);

        cont = new JButton("Tæsti");
        cont.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!doContinue) {
                    try {
                        float inter = Float.parseFloat(interval.getText());
                        SLEEP_BETWEEN_STEPS = (int)(inter*1000);
                        if (SLEEP_BETWEEN_STEPS < 0) {
                            throw new Exception("Step interval too small.");
                        }
                        doContinue = !doContinue;
                        ((JButton)ae.getSource()).setText("Stabdyti");
                        interval.setEnabled(false);
                        step.setEnabled(false);
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Þingsnio intervalas turi bûti didesnis arba lygus nuliui.", "Netinkamas þingsnio intervalas", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    ((JButton)ae.getSource()).setText("Tæsti");
                    interval.setEnabled(true);
                    step.setEnabled(true);
                    doContinue = !doContinue;
                }
            }
        });
        p.add(cont);

        p.add(new JLabel("Þingsnis kas:"));
        p.add(interval);
        p.add(new JLabel("s."));

        final JTextField diskNumber = new JTextField("3", 3);
        String[] algoTypes = { "Rekursyviai", "Iteraityviai"};
        final JComboBox algoList = new JComboBox(algoTypes);

        JButton reset = new JButton("Ið naujo");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    diskCount = Integer.parseInt(diskNumber.getText());
                    if (diskCount < 0 || diskCount > 100) {
                        throw new Exception("Disk number too small.");
                    }

                    doReset = true;
                    currentSolutionType = algoList.getSelectedIndex();

                    doContinue = false;
                    cont.setText("Tæsti");
                    step.setEnabled(true);
                    cont.setEnabled(true);
                    interval.setEnabled(true);
                    done.setVisible(false);

                    logArea.setText("Atlikti veiksmai:\n");
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Diskø skaièius turi bûti tarp 0 ir 100.", "Netinkamas diskø skaièius", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p.add(reset);

        p.add(algoList);

        p.add(new JLabel("Pradinis diskø skaièius:"));
        p.add(diskNumber);

        done = new JLabel("Iðspræsta");
        done.setOpaque(true);
        done.setBackground(new Color(0,255,0));
        done.setVisible(false);
        done.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        p.add(done);
        pane.add(p, BorderLayout.PAGE_END);
    }

    public static void addLog(String l) {
        addLog(l, false);
    }

    public static void addLog(String l, boolean important) {
        if (logAreaCheck.isSelected() || important) {
            logArea.append(l);
        }
        logArea.scrollRectToVisible(new Rectangle( 0, logArea.getHeight() + 10, 1, 1));
    }

    public static void setCaption(String c) {
        caption.setText(c);
    }

    public static String getExecTime() {
        return String.format("%.3f", (float)(new Date().getTime() - startedAt.getTime()) / 1000);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {

        //Create and set up the window.
        frame = new JFrame("Towers of Hanoi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
        //Use the content pane's default BorderLayout. No need for
        //setLayout(new BorderLayout());
        //Display the window.
        frame.pack();
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public static void start() {
        while (true) {
            x = new Peg("A", diskCount, diskCount);
            y = new Peg("C", diskCount, 0);
            z = new Peg("B", diskCount, 0);
            moveCount = 0;
            //actions.removeAllElements();
            doStep = false;
            doContinue = false;
            doReset = false;
            solved = false;

            switch (currentSolutionType) {
                case 0:
                    currentSolution = new RecursiveSolution(x, y, z, diskCount);
                    break;
                case 1:
                    currentSolution = new IterativeSolution(x, y, z, diskCount);
                    break;
            }

            da.setSource(currentSolution.getActions());
            startedAt = new Date();
            currentSolution.start();

            //System.out.println("start() end");
        }
    }

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            System.out.println("Look and feel was not found.");
        }

        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });

        while (da == null) {
            try {
                Thread.sleep(10);
            }
            catch (Exception e) {
            }
        }
        start();
    }
}