import java.util.Stack;

public class RecursiveSolution implements ISolution {

    public static Stack<IPaintable> actions;

    private Peg x;
    private Peg y;
    private Peg z;
    private int  diskCount;

    public RecursiveSolution(Peg x, Peg y, Peg z, int diskCount) {
        this.x = x;
        this.y = y;
        this.z = z;

        this.diskCount = diskCount;

        actions = new Stack<IPaintable>();

        TowersOfHanoi.setCaption("Rekursyvus sprendimas. Atvaizduojamas kiekvienas pagrindinės funkcijos iškvietimas.");
    }

    public void start() {
        Action newAction = new Action(x, y, z, getCurrAction(), 2, diskCount);
        actions.push(newAction);
        if (!TowersOfHanoi.repaintAndWait()) {return;}

        if (recursive(x, y, z, diskCount)) {
            TowersOfHanoi.solved = true;
            String execTime = TowersOfHanoi.getExecTime();
            TowersOfHanoi.addLog(String.format("Sprendimas baigtas per %s.\n", execTime), true);
        }

        if (!TowersOfHanoi.repaintAndWait()) {return;}
        actions.pop();
    }

    /**
     * The recursive solver.
     * Commented lines show actions.
     * All other lines are for step functionality and graphics.
     */
    private boolean recursive(Peg x, Peg y, Peg z, int n) {
        Action newAction = null;
        if (n > 0) {

            newAction = new Action(x, z, y, getCurrAction(), 1, n-1);
            actions.push(newAction);
            if (!TowersOfHanoi.repaintAndWait()) {return false;}

            // move n-1 disks from x to z
            recursive(x, z, y, n - 1);

            actions.pop();
            if (!TowersOfHanoi.repaintAndWait()) {return false;}

            newAction = new Action(x, y, z, getCurrAction(), 2, -1);
            actions.push(newAction);
            if (!TowersOfHanoi.repaintAndWait()) {return false;}

            TowersOfHanoi.moveCount++;

            // move top disk from x to y
            TowersOfHanoi.addLog(String.format("%4d. Perkeliamas %d diskas nuo %s ant %s\n", TowersOfHanoi.moveCount, x.getTopDiskNo(), x.name, y.name));
            y.disks.push(x.disks.pop());

            if (!TowersOfHanoi.repaintAndWait()) {return false;}
            actions.pop();
            if (!TowersOfHanoi.repaintAndWait()) {return false;}

            newAction = new Action(z, y, x, getCurrAction(), 3, n-1);
            actions.push(newAction);
            if (!TowersOfHanoi.repaintAndWait()) {return false;}

            // move n-1 disks from z to y
            recursive(z, y, x, n - 1);

            actions.pop();
            if (!TowersOfHanoi.repaintAndWait()) {return false;}
        }

        return true;
    }

    public Action getCurrAction() {
        try {
            return (Action)actions.peek();
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Stack<IPaintable> getActions() {
        return actions;
    }
}