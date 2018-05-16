package lt.dvim.toh;

import java.awt.Color;
import java.awt.Container;
import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.util.Date;
import java.util.ResourceBundle;

public class TowersOfHanoi extends JPanel
{
    static ResourceBundle messages = ResourceBundle.getBundle("toh");
    public static int SLEEP_BETWEEN_STEPS = 2000;

    public static int diskCount = 3;

    static {
        System.out.println(System.getProperty("user.language"));
        System.out.println(java.util.Locale.getDefault());
    }

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

            // wait when it is solved
            while (solved && !doReset) {
                Thread.sleep(100);
            }

            if (doReset) {
                return false;
            }

        } catch (Exception ex) {
        }

        return true;
    }

    public static void addComponentsToPane(Container pane) {

        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel(messages.getString("container.doesnt.use.borderlayout")));
            return;
        }

        JPanel p = createCaptionPanel();
        pane.add(p, BorderLayout.PAGE_START);

        da = new DrawArea(null);
        pane.add(da, BorderLayout.CENTER);

        pane.add(createLogPanel(), BorderLayout.WEST);

        pane.add(createButtonPanel(), BorderLayout.PAGE_END);
    }

    private static JPanel createCaptionPanel() {
        JPanel p = new JPanel();
        caption = new JLabel("");
        caption.setFont(caption.getFont().deriveFont(Font.BOLD, 16));
        p.add(caption);
        return p;
    }

    private static JPanel createButtonPanel() {
        JPanel p;
        p = new JPanel();
        // this is needed because combo box selections are not displayed over the draw area sometimes
        p.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        step = new JButton(messages.getString("step"));
        step.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                doStep = true;
            }
        });
        p.add(step);

        final JTextField interval = new JTextField("2", 3);

        cont = new JButton(messages.getString("continue"));
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
                        ((JButton)ae.getSource()).setText(messages.getString("stop"));
                        interval.setEnabled(false);
                        step.setEnabled(false);
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, messages.getString("step.interval.must.be.greater.than.or.equal.zero"), messages.getString("improper.pitch.range"), JOptionPane.ERROR_MESSAGE);
                    }
                }
                else {
                    ((JButton)ae.getSource()).setText(messages.getString("continue"));
                    interval.setEnabled(true);
                    step.setEnabled(true);
                    doContinue = !doContinue;
                }
            }
        });
        p.add(cont);

        p.add(new JLabel(messages.getString("step.is")));
        p.add(interval);
        p.add(new JLabel("s."));

        final JTextField diskNumber = new JTextField("3", 3);
        String[] algoTypes = {messages.getString("recursively"), messages.getString("iteratively")};
        final JComboBox algoList = new JComboBox(algoTypes);

        JButton reset = new JButton(messages.getString("new"));
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    diskCount = Integer.parseInt(diskNumber.getText());
                    if (diskCount < 0 || diskCount > 100) {
                        throw new Exception(messages.getString("disk.number.too.small"));
                    }

                    doReset = true;
                    currentSolutionType = algoList.getSelectedIndex();

                    doContinue = false;
                    cont.setText(messages.getString("continue"));
                    step.setEnabled(true);
                    cont.setEnabled(true);
                    interval.setEnabled(true);
                    done.setVisible(false);

                    logArea.setText(messages.getString("actions.taken"));
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, messages.getString("number.of.disc.must.be.between.0.and.100"), messages.getString("incorrect.number.of.discs"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        p.add(reset);

        p.add(algoList);

        p.add(new JLabel(messages.getString("initial.number.of.discs")));
        p.add(diskNumber);

        done = new JLabel(messages.getString("resolved"));
        done.setOpaque(true);
        done.setBackground(new Color(0,255,0));
        done.setVisible(false);
        done.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        p.add(done);
        return p;
    }

    private static JPanel createLogPanel() {
        JPanel p;
        p = new JPanel();
        p.setLayout(new BorderLayout());
        logArea = new JTextArea(messages.getString("actions.taken"));
        logArea.setFont(logArea.getFont().deriveFont(Font.PLAIN, 12));
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        p.add(scrollPane, BorderLayout.CENTER);
        logAreaCheck = new JCheckBox(messages.getString("register.actions"), true);
        p.add(logAreaCheck, BorderLayout.PAGE_END);
        return p;
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

        frame = new JFrame(messages.getString("towers.of.hanoi"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addComponentsToPane(frame.getContentPane());
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
        }
    }

    public static void main(String[] args)
    {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception ex) {
            System.out.println(messages.getString("look.and.feel.was.not.found"));
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