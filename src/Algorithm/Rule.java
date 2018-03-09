package Algorithm;

import java.util.LinkedList;

public class Rule {
    private String symbol = null;
    private LinkedList<String> parameterList = new LinkedList<>();
    private int noofParameters = 0;
    private final String varSequence;
    private LinkedList<Object> localTables = new LinkedList<>();
    private String[] code;
    private String transitionSymbol = "";
    private NewSymbol newSymbol;

    public Rule(String varSequence, LinkedList<Object> varTable, NewSymbol newSymbol) {
        this.varSequence = varSequence;
        localTables = varTable;
        this.newSymbol = newSymbol;
        for (int i = 0; i < varSequence.length(); i+=2) {
            parameterList.add(String.valueOf(varSequence.charAt(i)));
            noofParameters++;
        }
    }


    public String getTransitionSymbol() {
        return transitionSymbol;
    }

    public void setCode(Object termlist) {
    }

    public void pushVarTable(LinkedList<Object> objects) {
        localTables.addAll(objects);
    }

    public void popVarTable(){
        if(localTables.size()>0){
            localTables.clear();
        }
    }

    public void setNoofParameters(int noofParameters) {
        this.noofParameters = noofParameters;
    }

    public void setTransitionSymbol(String transitionSymbol) {
        this.transitionSymbol = transitionSymbol;
    }

    public void addLocalVariable(String token) {
            localTables.remove(token);
            parameterList.add(token);
            noofParameters++;
    }

    public void delLatestLocal() {
        localTables.removeLast();
        parameterList.removeLast();
        noofParameters--;
    }

    public void addLatestLocal(String token) {
        localTables.add(token);
        parameterList.add(token);
        noofParameters++;
    }

    public void setCode(String[] code){
        this.code = code;
    }



    @Override
    public String toString(){
        String string = "";
        for (int i = 0; i < parameterList.size(); i++) {
            string += parameterList.get(i) +",";
        }
        return "" + transitionSymbol + "[" + string + "]{" + /*code.toString()*/ "}";

    }

    public LinkedList<Object> makeExpressionEvaluator(String exp) {
        String symbol = newSymbol.getNewSymbol();
        String text = "";
        return null;
    }

    public LinkedList<Object> makeBooleanEvaluator(String exp) {
        String symbol = newSymbol.getNewSymbol();
        String text = "";
        return null;
    }
}

