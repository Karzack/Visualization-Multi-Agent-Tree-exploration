package Algorithm;


import java.util.HashMap;
import java.util.LinkedList;


public class Rule {
    private String symbol = null;
    private LinkedList<String> parameterList = new LinkedList<>();
    private int noofParameters = 0;
    private HashMap<String, Integer> localTables;
    private LinkedList<String> code;
    private String transitionSymbol = "";
    private NewSymbol newSymbol;

    public Rule(String varSequence, HashMap<String, Integer> varTable, NewSymbol newSymbol) {
        localTables = (HashMap<String, Integer>) varTable.clone();
        this.newSymbol = newSymbol;
        /*for (int i = 0; i < varSequence.length(); i+=2) {
            parameterList.add(String.valueOf(varSequence.charAt(i)));
            noofParameters++;
        }*/
    }


    public String getTransitionSymbol() {
        return transitionSymbol;
    }

    public void setCode(LinkedList<String> termList) {
        this.code = (LinkedList<String>) termList.clone();
        System.out.println(termList.toString());
    }

    public LinkedList<String> getCode() {
        return code;
    }

    public void pushVarTable(HashMap<String,Integer> objects) {
        localTables.putAll(objects);
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
        this.transitionSymbol = ""+transitionSymbol;
    }

    public void addLocalVariable(String token) {
        //localTables.remove(token);
        parameterList.add(token);
        noofParameters++;
    }

    public void setLocalVariable(String variable, int value){
        localTables.put(variable,value);
    }

    public void delLatestLocal() {
        parameterList.removeLast();
        noofParameters--;
    }

    public void addLatestLocal(String token) {
        localTables.put(token,0);
        parameterList.add(token);
        noofParameters++;
    }




    @Override
    public String toString(){
        String string = "";
        for (int i = 0; i < parameterList.size(); i++) {
            string += parameterList.get(i) +",";
        }
        return "" + transitionSymbol + "[" + string + "]{" + /*code.toString()*/ "}";

    }

    public LinkedList<String> makeExpressionEvaluator(String exp) {
        LinkedList<String> returner = new LinkedList<>();
        String symbol = newSymbol.getNewSymbol();
        String expression = Commands.JavaCodeParser.parseCoder(exp);
        String code =
                "   public int activate" + symbol +  "(HashMap<String,Integer> vars){ \n " +
                "       return " + expression + "; \n " +
                "} \n";
        returner.add(symbol);
        returner.add(code);
        return returner;
    }

    public LinkedList<String> makeBooleanEvaluator(String exp) {
        LinkedList<String> returner = new LinkedList<>();
        String symbol = newSymbol.getNewSymbol();
        String expression = Commands.JavaCodeParser.parseCoder(exp);
        String code =
                "   public boolean activate" + symbol + "(HashMap<String,Integer> vars){ \n " +
                "       return " + expression + "; \n " +
                "} \n";

        returner.add(symbol);
        returner.add(code);
        return returner;
    }


    public HashMap<String, Integer> getLocalVariables() {
        return localTables;
    }

    public LinkedList<String> getParameters() {
        return parameterList;
    }
}

