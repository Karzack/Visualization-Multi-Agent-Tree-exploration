package Algorithm;


import javax.sound.midi.Soundbank;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class AlgorithmParser {
    private String valid = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private String varSequence ="i,k,t,m,j";
    private Scanner scanner = new Scanner(
            "Root->^i=1..k{X[t][floor(i/(log(i)+1))]};" +
                    "X[m][j]->?m>0{X[m-1][j]}{B[j]};" +
                    "B[j]->?j>0{^i=2..t{T}B[j-1]}{^i=1..t{T}};");

            /*
            "Root->?floor(i/(log(i)+1))>1{1}{i};" +
            ^i=1..k{X[t][]}
            "T->S;" +
            "Q[l]->T^k=1..10{Q[l-1]};" +
            "R[a][b][c][d]->?a<b*c-d{T[a-1]^i=b..c+d{F F G}}{T[a-d]};" +
            "S[k][l][m]->A[k-1]B[l/2][m+1]X[k/2-l+1]D;"
            */
    private HashMap<String, Integer> varTable = new HashMap<>();
    private LinkedList<String> symTable = new LinkedList<>();
    private HashMap<String,Rule> ruleTable = new HashMap<>();
    private NewSymbol newSymbol = new NewSymbol();
    // private String currentScan = "";
    //private int scanIndex;
    private StringBuilder currentScan;
    private File root;
    private File sourceFile;
    private String code = "package Commands;\n" +
            "import static java.lang.Math.*; \n" +
            "import java.util.HashMap; \n" +
            "public class ActionList{" + " \n ";
    Class<?> actionList;
    Object instance;



    public AlgorithmParser(){
       varTable.put("i",24);
       varTable.put("k",4);
       varTable.put("t",8);
       varTable.put("m",12);
       varTable.put("j",7);


    }

    public HashMap parse() throws IOException, ClassNotFoundException {
        root = new File("src/Commands");
        sourceFile = new File(root, "/ActionList.java");
        sourceFile.getParentFile().mkdirs();
        scanner.useDelimiter(";");
        ruleSeq();
        createActionListFile();
        compileActionList();
        System.out.println("Post kompileringsstrnäng!");
        instanciateActionList();
        System.out.println("Post Instans av klass");
        executeRuleNoParam(ruleTable.get("Root"));
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

        if (currentScan.indexOf("->")==0) {
            currentScan.delete(0,2);
            LinkedList<String> termlist = null;
            try {
                termlist = termExpSeq(rule);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            rule.setCode(termlist);
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
        if (valid.contains(token)){
            rule.addLocalVariable(token);
            if(!symTable.contains(token)) {
                symTable.add(token);
            }
        }
        else{
            System.out.println("error, localVar variable error, not in table: " + token);
        }
    }
    private LinkedList<String> termExpSeq(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<String> term = termExp(rule);
        if(currentScan.length()>0) {
            if (currentScan.charAt(0) != '}') {
                term.addAll(termExpSeq(rule));
                return term;
            }
            return term;
        }
        return term;
    }

    private LinkedList<String> termExp(Rule rule) throws IOException, ClassNotFoundException {
        String token = String.valueOf(currentScan.charAt(0));
        if(token.equals("?")){
            return queryExp(rule);
        }
        else if(token.equals("^")){
            return loopExp(rule);
        }
        else if(valid.contains(token)){
            return symExp(rule);
        }
        System.out.println("TermExp error, Invalid expression");
        return null;
    }

    private LinkedList<String> loopExp(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<String> returner = new LinkedList<>();
        returner.add("^");
        if(currentScan.indexOf("^")==0) {
            currentScan.deleteCharAt(0);
            String token = String.valueOf(currentScan.charAt(0));
            currentScan.deleteCharAt(0);
            returner.add(token);
            if (valid.contains(token)) {
                if (currentScan.indexOf("=") == 0) {
                    currentScan.deleteCharAt(0);
                    rule.addLatestLocal(token);
                    LinkedList<String> exp1 = innerExp("..", rule);
                    returner.addAll(exp1);
                    if (currentScan.indexOf("..") == 0) {
                        currentScan.delete(0, 2);
                        LinkedList exp2 = innerExp("{", rule);
                        returner.addAll(exp2);
                        if (currentScan.indexOf("{") == 0) {
                            currentScan.deleteCharAt(0);
                            LinkedList<String> repetition = termExpSeq(rule);
                            returner.addAll(repetition);
                            if (currentScan.indexOf("}") != 0) {
                                System.out.println("Error: Loop error 1");
                            } else {
                                currentScan.deleteCharAt(0);
                                rule.delLatestLocal();
                                return returner;
                            }

                        }
                    }
                }
            }
            return returner;
        }else{
            System.out.println("Error: Expected \"^\" ");
            return null;
        }
    }

    private LinkedList<String> queryExp(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<String> returner = new LinkedList<>();
        if(currentScan.indexOf("?")==0) {
            currentScan.deleteCharAt(0);
            returner.add("?");
            LinkedList bexp = boolExp(rule);
            returner.addAll(bexp);
            if (currentScan.indexOf("{") == 0) {
                currentScan.deleteCharAt(0);
                LinkedList<String> ifSelection = termExpSeq(rule);
                returner.addAll(ifSelection);
                if (currentScan.charAt(0)!='}') {
                    System.out.println("error queryError 1: ");
                }
                else{
                    currentScan.deleteCharAt(0);
                }
            } else {
                System.out.println("queryExp error");
            }
            if (currentScan.indexOf("{") == 0) {
                currentScan.deleteCharAt(0);
                LinkedList elSelection = termExpSeq(rule);
                returner.add("else");
                returner.addAll(elSelection);
                String token = String.valueOf(currentScan.charAt(0));
                if (!token.equals("}")) {
                    System.out.println("error queryError 2: " + token);
                }
            }
        }
        return returner;
    }

    private LinkedList<String> symExp(Rule rule) throws IOException, ClassNotFoundException {
        LinkedList<String> returner = new LinkedList<>();
        String token = String.valueOf(currentScan.charAt(0));
        currentScan.deleteCharAt(0);
        returner.add("S");
        returner.add(token);
        if(valid.contains(token)){
            LinkedList<String> actualList = new LinkedList();
            if(currentScan.indexOf("}")!=-1 && currentScan.indexOf("}")!=0){
                actualList = innerExpSeq(rule);
            }
            returner.addAll(actualList);
            return returner;
        }
        return returner;
    }

    private LinkedList<String> innerExpSeq(Rule rule) {
        LinkedList<String> returner = new LinkedList<>();
        if(currentScan.indexOf("[")==0){
            currentScan.deleteCharAt(0);
            LinkedList expCode = innerExp("]", rule);
            currentScan.deleteCharAt(0);
            returner.addAll(expCode);
            if(currentScan.indexOf("[")==0){
                currentScan.deleteCharAt(0);
                expCode = innerExp("]",rule);
                currentScan.deleteCharAt(0);
                returner.addAll(expCode);
            }
            if(currentScan.charAt(0)!='}'){
                returner.addAll(innerExpSeq(rule));
                return returner;
            }
        }
        return returner;
    }

    private LinkedList<String> innerExp(String follow, Rule rule) {
        String exp = "";
        while (currentScan.indexOf(follow)!=0){
            exp += currentScan.charAt(0);
            currentScan.deleteCharAt(0);
        }

        LinkedList<String> returner = rule.makeExpressionEvaluator(exp);
        code += returner.get(1);
        returner.remove(1);
        return returner;
    }

    private LinkedList<String> boolExp(Rule rule) {
        String exp = "";
        while (currentScan.indexOf("{")!=0){
            exp += currentScan.charAt(0);
            currentScan.deleteCharAt(0);
        }
        LinkedList<String> returner = rule.makeBooleanEvaluator(exp);
        code += returner.get(1);
        returner.remove(1);
        return returner;
    }
    public void createActionListFile() throws IOException {
        code += "}";
        Files.write(sourceFile.toPath(),
                code.getBytes(StandardCharsets.UTF_8));
    }

    public void compileActionList(){

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null,null,null,sourceFile.getPath());
    }

    public void instanciateActionList() throws ClassNotFoundException, MalformedURLException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        actionList = Class.forName("Commands.ActionList", true, classLoader);
        try {
            instance = actionList.newInstance();
            //method = actionList.getMethod("activate2",HashMap.class);
            //method.invoke(instance,varTable);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }




    public void executeRuleNoParam(Rule rule) {
        LinkedList<String> executor = rule.getCode();
        int index = 0;
            switch (executor.get(index)) {
                case "^":
                    System.out.println("start Loop");
                    executeLoop(index,executor,rule);
                    System.out.println("Loop Finished");
                    break;
                case "?":
                    System.out.println("start Query");
                    executeQuery(index,executor,rule);
                    System.out.println("Query Finished");
                    break;
                case "S":
                    System.out.println("start Rule");
                    executeSymbol(index,executor,rule);
                    System.out.println("Rule Finished");
                    break;
            }
    }



    public void executeRuleOneParam(Rule rule,int param1) {
        LinkedList<String> executor = rule.getCode();
        rule.setLocalVariable(rule.getParameters().get(0),param1);
        int index = 0;
            switch (executor.get(index)) {
                case "^":
                    System.out.println("start Loop");
                    executeLoop(index,executor,rule);
                    System.out.println("Loop Finished");
                    break;
                case "?":
                    System.out.println("start Query");
                    executeQuery(index,executor,rule);
                    System.out.println("Query Finished");
                    break;
                case "S":
                    System.out.println("start Rule");
                    executeSymbol(index,executor,rule);
                    System.out.println("Rule Finished");
                    break;
            }
    }


    private void executeRuleTwoParam(Rule rule, int param1, int param2) {
        LinkedList<String> executor = rule.getCode();
        rule.setLocalVariable(rule.getParameters().get(0),param1);
        rule.setLocalVariable(rule.getParameters().get(1),param2);
        int index = 0;
            switch (executor.get(index)) {
                case "^":
                    System.out.println("start Loop");
                    executeLoop(index,executor,rule);
                    System.out.println("Loop Finished");

                    break;
                case "?":
                    System.out.println("start Query");
                    executeQuery(index,executor,rule);
                    System.out.println("Query Finished");
                    break;
                case "S":
                    System.out.println("start Rule");
                    executeSymbol(index,executor,rule);
                    System.out.println("Rule Finished");
                    break;
            }
    }

    public int executeExpression(int symbol, HashMap<String,Integer> variables){
        try {
            //instance = actionList.newInstance();
            Method method = actionList.getDeclaredMethod("activate"+symbol,HashMap.class);
            return (int) method.invoke(instance,variables);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean executeBoolean(int symbol, HashMap<String, Integer> variables) {
        try {
            //instance = actionList.newInstance();
            Method method = actionList.getDeclaredMethod("activate"+symbol,HashMap.class);
            return (boolean) method.invoke(instance,variables);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int executeQuery(int index, LinkedList<String> executor, Rule rule) {
        if(executeBoolean(Integer.parseInt(executor.get(index+1)),rule.getLocalVariables())){
            index+=2;
            switch (executor.get(index)){
                case "^":
                    index = executeLoop(index,executor,rule) +1;
                    break;
                case "?":
                    index = executeQuery(index,executor,rule)+1;
                    break;
                case "S":
                    index = executeSymbol(index,executor,rule)+1;
            }
        }else{
            while (!executor.get(index).equals("else")){
                index++;
            }
            index++;
            switch (executor.get(index)){
                case "^":
                    index = executeLoop(index,executor,rule);
                    index++;
                    break;
                case "?":
                    index = executeQuery(index,executor,rule);
                    index++;
                    break;
                case "S":
                    index = executeSymbol(index,executor,rule);
                    index++;
            }
        }
        return index;
    }

    private int executeSymbol(int index, LinkedList<String> executor, Rule rule) {
        if(index+2<executor.size()) {
            if(index+3<executor.size()) {
                if (!valid.contains(executor.get(index + 3)) && !executor.get(index + 3).equals("^") && !executor.get(index + 3).equals("?")) {
                    //Execute Rule with 2 Parameters!
                    int paramOne = executeExpression(Integer.parseInt(executor.get(index + 2)), rule.getLocalVariables());
                    int paramTwo = executeExpression(Integer.parseInt(executor.get(index + 3)), rule.getLocalVariables());
                    index++;
                    System.out.println("execute symbol: " + executor.get(index).toString() + "[" + paramOne + "][" + paramTwo + "]");

                    executeRuleTwoParam(ruleTable.get(executor.get(index)), paramOne, paramTwo);
                    index += 3;
                }
            } else {
                    if (!valid.contains(executor.get(index + 2)) && !executor.get(index + 2).equals("^") && !executor.get(index + 2).equals("?")) {
                        //Execute Rule with one Parameter!
                        int paramOne = executeExpression(Integer.parseInt(executor.get(index + 2)), rule.getLocalVariables());
                        index++;
                        System.out.println("execute symbol: " + executor.get(index).toString() + "[" + paramOne + "]");

                        executeRuleOneParam(ruleTable.get(executor.get(index)), paramOne);
                        index += 2;
                    } else {
                        //Execute Rule without Parameters
                        index++;
                        System.out.println("execute symbol: " + executor.get(index).toString());

                        executeRuleNoParam(ruleTable.get(executor.get(index)));
                        index++;
                    }
                }
            } else {
                    //Execute Rule without Parameters
                    index++;
                    System.out.println("execute symbol: " + executor.get(index).toString());
                    executeRuleNoParam(ruleTable.get(executor.get(index)));
                    index++;
                }

        return index;
    }
    private int executeLoop(int index, LinkedList<String> executor, Rule rule) {
        int iterator = Integer.parseInt(executor.get(index + 2));
        int top = executeExpression(Integer.parseInt(executor.get(index+3)),rule.getLocalVariables());
        index += 4;
        for (int i = iterator; i < top; i++) {
            rule.setLocalVariable("i",i);
            switch (executor.get(index)){
                case "^":
                    System.out.println("Execute Loop");
                    executeLoop(index,executor,rule);
                    System.out.println("Ending Loop");
                    break;
                case "?":
                    System.out.println("Execute Query");
                    executeQuery(index,executor,rule);
                    System.out.println("Ening Query");

                    break;
                case "S":
                    System.out.println("Execute Symbol");
                    executeSymbol(index,executor,rule);
                    System.out.println("Ending Symbol");
                    break;
            }

        }
        return index;
    }
}



