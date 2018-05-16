package lt.dvim.toh;

import java.util.Stack;

interface ISolution {

    public Stack<IPaintable> getActions();

    public void start();
}
