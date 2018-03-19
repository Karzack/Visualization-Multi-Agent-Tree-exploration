package Algorithm;

import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.util.*;

public class AlgorithmParser {
    private String valid = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private String varSequence ="a,b,c,d,e,g,h,i,j,k,l,m";
    private Scanner scanner = new Scanner("Root->^i=1..k{X[t][floor(i/(log(i,2)+1))]};" +
            "X[m][j]->?m>0{X[m-1][j]}{B[j]};" +
            "B[j] -> ?j>0{^i=2..t{T}B[j-1]} {^i=1..t{T}};"
            /*"T->S;" +
            "Q[l]->T^k=1..10{Q[l-1]};" +
            "R[a][b][c][d]->?a<b*c-d{T[a-1]^i=b..c+d{F F G}}{T[a-d]};" +
            "S[k][l][m]->A[k-1]B[l/2][m+1]X[k/2-l+1]D;"
            */);
    private HashMap<String, Integer> varTable = new HashMap<>();
    private LinkedList<String> symTable = new LinkedList<>();
    private HashMap<String,Rule> ruleTable = new HashMap<>();
    private NewSymbol newSymbol = new NewSymbol();
   // private String currentScan = "";
    //private int scanIndex;
    private StringBuilder currentScan;



    public AlgorithmParser(){
        varTable.put("a",0);
        varTable.put("b",0);
        varTable.put("c",0);
        varTable.put("d",0);
        varTable.put("j",0);
        varTable.put("k",0);
        varTable.put("l",0);
        varTable.put("m",0);
    }

    public HashMap parse(){
        scanner.useDelimiter(";");
        ruleSeq();

        return ruleTable;
    }

    public void ruleSeq() {
        if (scanner.hasNext()) {
            currentScan = new StringBuilder(scanner.next());
            if (currentScan.length() > 0) {
                Rule rule = rule();
                if (scanner.hasNext()) {
                    ruleSeq();
                }
                ruleTable.put(rule.getTransitionSymbol(), rule);
            }
        }
    }

    private Rule rule() {
        Rule rule = new Rule(varSequence,varTable, newSymbol);
        leftSide(rule);

        try {
            if (currentScan.indexOf("->")==0) {
                currentScan.delete(0,2);
                LinkedList<Object> termlist = termExpSeq(rule);
                rule.setCode(termlist);
            }
        }
        catch (Exception e){

        }
        return rule;
    }



    private void leftSide(Rule rule) {
        String sym = "";
        while (valid.contains(String.valueOf(currentScan.charAt(0)))) {
            sym += String.valueOf(currentScan.charAt(0));
            currentScan.deleteCharAt(0);
        }
        if(sym.length()>0) {
            if (currentScan.indexOf("[")==0) {
                currentScan.deleteCharAt(0);
                rule.pushVarTable(new HashMap<>());
                rule.setNoofParameters(0);
                localSeq(rule);
            } else if (currentScan.indexOf("->")!=0) {
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
            if(currentScan.indexOf("]")==0){
                currentScan.deleteCharAt(0);
                if (currentScan.indexOf("[")==0){
                    currentScan.deleteCharAt(0);
                    localSeq(rule);
                }
                else if(currentScan.indexOf("->")!=0){
                    System.out.println("LocalSeq error");
                }
            }
            else{
                System.out.println("localSeq error, exptecting ']'");
            }
        }


    private void local(Rule rule) {
            String token = String.valueOf(currentScan.charAt(0));
            currentScan.deleteCharAt(0);
            if (!symTable.contains(token)){
                rule.addLocalVariable(token);
                symTable.add(token);
            }
            else{
                System.out.println("error, localVar variable error, not in table: " + token);
            }
    }
    private LinkedList<Object> termExpSeq(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<Object> term = termExp(rule);
        if(currentScan.length()>0){
            term.add(termExpSeq(rule));
            return term;
        }
        return term;
    }
    private LinkedList<Object> termExp(Rule rule) throws IOException, ClassNotFoundException {
        String token = String.valueOf(currentScan.charAt(0));
        if(token.equals("?")){
            currentScan.deleteCharAt(0);
            return queryExp(rule);
        }
        else if(token.equals("^")){
            currentScan.deleteCharAt(0);
            return loopExp(rule);
        }
        else if(valid.contains(token)){
            return symExp(rule);
        }
        currentScan.deleteCharAt(0);
        return null;
    }

    private LinkedList<Object> loopExp(Rule rule) throws IOException, ClassNotFoundException {
            LinkedList<Object> returner = new LinkedList<>();
            returner.add("^");
            String token = String.valueOf(currentScan.charAt(0));
            currentScan.deleteCharAt(0);
            returner.add(token);
            if (valid.contains(token)) {
                if (currentScan.indexOf("=")==0){
                    currentScan.deleteCharAt(0);
                    rule.addLatestLocal(token);
                    LinkedList<Object> exp1 = innerExp("..", rule);
                    returner.add(exp1);
                    if (currentScan.indexOf("..")==0) {
                        currentScan.delete(0,2);
                        LinkedList exp2 = innerExp("{", rule);
                        returner.add(exp2);
                        if (currentScan.indexOf("{")==0) {
                            LinkedList<Object> repetition = termExpSeq(rule);
                            returner.add(repetition);
                            if (currentScan.indexOf("}")!=0) {
                                System.out.println("Error: Loop error 1");
                            }
                            rule.delLatestLocal();
                            return returner;

                        }
                    }
                }
            }
        return returner;
    }

    private LinkedList<Object> queryExp(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<Object> returner = new LinkedList<>();
        returner.add("?");
        LinkedList bexp = boolExp(rule);
        returner.add(bexp);
        if(currentScan.indexOf("[")==0){
            currentScan.deleteCharAt(0);
            LinkedList<Object> ifSelection = termExpSeq(rule);
            returner.add(ifSelection);
            String token = String.valueOf(currentScan.charAt(0));
            currentScan.deleteCharAt(0);
            if(!token.equals("}")){
                System.out.println("error queryError 1: " + token);
            }
        }
        else{
            System.out.println("queryExp error");
        }
        if (currentScan.indexOf("{")==0){
            currentScan.deleteCharAt(0);
            LinkedList elSelection = termExpSeq(rule);
            returner.add(elSelection);
            String token = String.valueOf(currentScan.charAt(0));
            if(!token.equals("}")){
                System.out.println("error queryError 2: " + token);
            }
        }

        return returner;
    }

    private LinkedList<Object> symExp(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<Object> returner = new LinkedList<>();
        String token = String.valueOf(currentScan.charAt(0));
        currentScan.deleteCharAt(0);
        returner.add("S");
        returner.add(token);
        if(valid.contains(token)){
            LinkedList<Object> actualList = new LinkedList();
            if(currentScan.indexOf("}")==-1){
                actualList = innerExpSeq(rule);

            }
            else if(currentScan.indexOf("}")==0) {
                currentScan.deleteCharAt(0);
            }
            returner.add(actualList);
            return returner;
        }
        return returner;
    }

    private LinkedList<Object> innerExpSeq(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<Object> returner = new LinkedList<>();
        if(currentScan.indexOf("[")==0){
            currentScan.deleteCharAt(0);
            LinkedList expCode = innerExp("]", rule);
            returner.add(expCode);
            if(currentScan.length()<=0){
                returner.add(innerExpSeq(rule));
                return returner;
            }
        }
        return returner;
    }

    private LinkedList<Object> innerExp(String follow, Rule rule) throws IOException, ClassNotFoundException {
        String exp = "";
        while (currentScan.indexOf(follow)!=0){
            exp += currentScan.charAt(0);
            currentScan.deleteCharAt(0);
        }
        /*if(follow.length()>1) {
            currentScan.delete(0, follow.length());
        }else{
            currentScan.deleteCharAt(0);
        }*/
        return rule.makeExpressionEvaluator(exp);
    }

    private LinkedList<Object> boolExp(Rule rule) throws IOException, ClassNotFoundException {
        String exp = "";
        while (currentScan.indexOf("{")!=0){
            exp += currentScan.charAt(0);
            currentScan.deleteCharAt(0);
        }
        return rule.makeBooleanEvaluator(exp);
    }

}



