package lt.dvim.toh;

import java.util.ResourceBundle;
import java.util.Stack;
import java.util.EmptyStackException;

public class IterativeSolution implements ISolution {

    ResourceBundle messages = ResourceBundle.getBundle("toh");
    public static Stack<IPaintable> pegs;

    private Peg x;
    private Peg y;
    private Peg z;
    private int  diskCount;

    public IterativeSolution(Peg x, Peg y, Peg z, int diskCount) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.diskCount = diskCount;

        pegs = new Stack<IPaintable>();
        pegs.push(new Cycle(x, y, z, diskCount % 2));
    }

    public void start() {
        // apply different solve rules by the disk count
        // rules have been taken from here:
        // http://en.wikipedia.org/wiki/Tower_of_Hanoi#Iterative_solution
        if (diskCount % 2 == 0) {
            while (true) {
                if (!TowersOfHanoi.repaintAndWait()) {return;}
                legalMove(x, z);
                if (!TowersOfHanoi.repaintAndWait()) {return;}
                legalMove(x, y);
                if (!TowersOfHanoi.repaintAndWait()) {return;}
                legalMove(y, z);

                // when the disk count is even, then the puzzle will be finished after last move,
                // so check for completion here
                if (x.disks.empty() && z.disks.empty()) {
                    TowersOfHanoi.solved = true;
                    String execTime = TowersOfHanoi.getExecTime();
                    TowersOfHanoi.addLog(String.format(messages.getString("solution.completed"), execTime), true);
                }
            }
        }
        else {
            // make the legal move between pegs x and y
            while (true) {
                if (!TowersOfHanoi.repaintAndWait()) {return;}
                legalMove(x, y);

                // when the disk count is odd, then the puzzle will be fisished after the first move in this cycle,
                // so check for completion here
                if (x.disks.empty() && z.disks.empty()) {
                    TowersOfHanoi.solved = true;
                    String execTime = TowersOfHanoi.getExecTime();
                    TowersOfHanoi.addLog(String.format(messages.getString("solution.completed"), execTime), true);
                }
                if (!TowersOfHanoi.repaintAndWait()) {return;}

                legalMove(x, z);
                if (!TowersOfHanoi.repaintAndWait()) {return;}
                legalMove(y, z);
            }
        }

        //System.out.println("solved");
    }

    /**
     * Make a legal move between pegs a and b
     *
     * Legal move is one of the following:
     *   - smaller disk on the bigger disk
     *   - any disk on the empty peg
     *
     * @param a first peg
     * @param b second peg
     */
    public void legalMove(Peg a, Peg b) {
        int diskOnA;
        int diskOnB;

        // get the top disk from the Peg a
        try {
            diskOnA = a.disks.peek();
        }
        catch (EmptyStackException ex) {
            diskOnA = -1;
        }

        // get the top disk from the Peg b
        try {
            diskOnB = b.disks.peek();
        }
        catch (EmptyStackException ex) {
            diskOnB = -1;
        }

        TowersOfHanoi.moveCount++;

        if (diskOnA > diskOnB) {
            // move disk from a to b
            TowersOfHanoi.addLog(String.format(messages.getString("transferred.from.disk"), TowersOfHanoi.moveCount, a.getTopDiskNo(), a.name, b.name));
            b.disks.push(a.disks.pop());
        }
        else {
            // move disk from b to a
            TowersOfHanoi.addLog(String.format(messages.getString("transferred.from.disk"), TowersOfHanoi.moveCount, b.getTopDiskNo(), b.name, a.name));
            a.disks.push(b.disks.pop());
        }
    }

    public Stack<IPaintable> getActions() {
        return pegs;
    }
}
