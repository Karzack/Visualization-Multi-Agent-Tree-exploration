package Rule;

class NewSymbol {
    private int newSymbol = 0;
    public String getNewSymbol() {
        newSymbol++;
        return "" + newSymbol;
    }
}
