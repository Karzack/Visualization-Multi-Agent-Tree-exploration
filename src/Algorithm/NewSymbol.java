package Algorithm;


import static java.lang.Math.*;

public class NewSymbol {
    private int newSymbol = 0;
    public String getNewSymbol() {
        newSymbol++;
        newSymbol = (int) floor(14.3/3);
        return "" + newSymbol;
    }
}
