package Algorithm;

import sun.awt.image.ImageWatched;

import java.util.*;

public class AlgorithmParser {
    private String valid = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private String varSequence ="a,b,c,d,k,l,m";
    private Scanner scanner = new Scanner("S[k]->S[k/2];" +
            "T->S;" +
            "Q[l]->T^k=1..10{Q[l-1]};" +
            "R[a][b][c][d]->?a<b*c-d{T[a-1]^i=b..c+d{F F G}}{T[a-d]};" +
            "S[k][l][m]->A[k-1]B[l/2][m+1]X[k/2-l+1]D;");
    private LinkedList<Object> varTable = new LinkedList<>();
    private LinkedList<Object> symTable = new LinkedList<>();
    private HashMap<String,Rule> ruleTable = new HashMap<>();
    private NewSymbol newSymbol = new NewSymbol();
    private String currentScan = "";
    private int scanIndex;


    public AlgorithmParser(){

    }

    public HashMap parse(){
        scanner.useDelimiter(";");
        ruleSeq();
        return ruleTable;
    }

    public void ruleSeq() {
        if (scanner.hasNext()) {
            currentScan = scanner.next();
            scanIndex = 0;
            Rule rule = rule();
            if (scanner.hasNext()) {
                ruleSeq();
            }
            ruleTable.put(rule.getTransitionSymbol(), rule());
        }

    }

    private Rule rule() {
        Rule rule = new Rule(varSequence,varTable, newSymbol);
        leftSide(rule);
        try {
            if (currentScan.substring(scanIndex,scanIndex+2).equals("->")) {
                scanIndex+=2;
                LinkedList<Object> termlist = termExpSeq(rule);
                rule.setCode(termlist);
            }
        }
        catch (Exception e){

        }
        return rule;
    }



    private void leftSide(Rule rule) {
        String sym = String.valueOf(currentScan.charAt(scanIndex));
        scanIndex++;
        if(valid.contains(sym)) {
            if (currentScan.charAt(scanIndex)=='[') {
                scanIndex++;
                rule.pushVarTable(new LinkedList<Object>());
                rule.setNoofParameters(0);
                localSeq(rule);
            } else if (!currentScan.substring(scanIndex,scanIndex+2).equals("->")) {
                //error LeftSide error
            }
            if (ruleTable.containsKey(sym)) {
                System.out.println("Symbol already exists");
            } else {
                rule.setTransitionSymbol(sym);
            }
        }
        else{
            System.out.println("error, not valid symbol (not in table)");
        }
    }

    private void localSeq(Rule rule) {
            local(rule);
            if(currentScan.charAt(scanIndex)==']'){
                scanIndex++;
                if (currentScan.charAt(scanIndex)=='['){
                    localSeq(rule);
                }
                else if(!currentScan.substring(scanIndex,scanIndex+2).equals("->")){
                    System.out.println("LocalSeq error");
                }
            }
            else{
                System.out.println("localSeq error, exptecting ']'");
            }
        }


    private void local(Rule rule) {
            String token = String.valueOf(currentScan.charAt(scanIndex));
            scanIndex++;
            if (!symTable.contains(token)){
                rule.addLocalVariable(token);
            }
            else{
                System.out.println("error, localVar variable error, not in table: " + token);
            }
    }
    private LinkedList<Object> termExpSeq(Rule rule) {
        LinkedList<Object> term = termExp(rule);
        if(!currentScan.substring(scanIndex,scanIndex+11).equals("termExpSeq")){
            term.add(termExpSeq(rule));
            currentScan+=11;
            return term;
        }
        currentScan+=11;
        return term;
    }
    private LinkedList<Object> termExp(Rule rule){
        String token = String.valueOf(currentScan.charAt(scanIndex));
        scanIndex++;
        if(token.equals("?")){
            return queryExp(rule);
        }
        else if(token.equals("^")){
            return loopExp(rule);
        }
        return null;
    }

    private LinkedList<Object> loopExp(Rule rule) {
            LinkedList<Object> returner = new LinkedList<>();
            returner.add("^");
            String token = String.valueOf(currentScan.charAt(scanIndex));
        scanIndex++;
            returner.add(token);
            if (!symTable.contains(token)) {
                if (currentScan.charAt(scanIndex)=='=') {
                    rule.addLatestLocal(token);
                    LinkedList<Object> exp1 = innerExp("..", rule);
                    returner.add(exp1);
                    if (scanner.hasNext("..")) {
                        scanner.next("..");
                        LinkedList exp2 = innerExp("{", rule);
                        returner.add(exp2);
                        if (scanner.next().equals("{")) {
                            LinkedList repetition = termExpSeq(rule);
                            returner.add(repetition);
                            if (!scanner.next().equals("}")) {
                                System.out.println("Error: Loop error 1");
                            }
                            rule.delLatestLocal();
                            return returner;

                        }
                    }
                }
            }
        return null;
    }

    private LinkedList<Object> queryExp(Rule rule){
        LinkedList<Object> returner = new LinkedList<>();
        returner.add("?");
        LinkedList bexp = boolExp(rule);
        returner.add(bexp);
        if(currentScan.charAt(scanIndex)=='['){
            scanIndex++;
            LinkedList<Object> ifSelection = termExpSeq(rule);
            returner.add(ifSelection);
            String token = String.valueOf(currentScan.charAt(scanIndex));
            scanIndex++;
            if(!token.equals("}")){
                System.out.println("error queryError 1: " + token);
            }
        }
        else{
            System.out.println("queryExp error");
        }
        if (scanner.next().equals("{")){
            LinkedList elSelection = termExpSeq(rule);
            returner.add(elSelection);
            String token = scanner.next();
            if(!token.equals("}")){
                System.out.println("error queryError 2: " + token);
            }
        }

        return returner;
    }

    private LinkedList<Object> symExp(Rule rule){
        LinkedList<Object> returner = new LinkedList<>();
        returner.add("S");
        String token = scanner.next();
        returner.add(token);
        if(!symTable.contains(token)){
            LinkedList<Object> actualList = new LinkedList();
            if(!scanner.hasNext("termExp")){
                actualList = innerExpSeq(rule);

            }
            returner.add(actualList);
            return returner;
        }
        return null;
    }

    private LinkedList<Object> innerExpSeq(Rule rule){
        LinkedList<Object> returner = new LinkedList<>();
        if(scanner.next().equals("[")){
            LinkedList expCode = innerExp("]", rule);
            returner.add(expCode);
            if(!scanner.hasNext("innerExpSeq")){
                returner.add(innerExpSeq(rule));
                return returner;
            }
        }
        return null;
    }

    private LinkedList<Object> innerExp(String follow, Rule rule){
        String exp = "";
        while (scanner.hasNext("]")){
            exp += scanner.next();
        }
        return rule.makeExpressionEvaluator(exp);
    }

    private LinkedList<Object> boolExp(Rule rule){
        String exp = "";
        while (scanner.hasNext("]")){
            exp += scanner.next();
        }
        return rule.makeBooleanEvaluator(exp);
    }

}



