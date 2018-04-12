package Algorithm;


import GUI.GUI;

import javax.swing.*;
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

public class AlgorithmParser {
    private String valid = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private GUI gui;
    private String varSequence;
    private Scanner scanner;


    private HashMap<String, Integer> varTable = new HashMap<>();
    private LinkedList<String> symTable = new LinkedList<>();
    private HashMap<String,Rule> ruleTable = new HashMap<>();
    private NewSymbol newSymbol = new NewSymbol();
    // private String currentScan = "";
    //private int scanIndex;
    private StringBuilder currentScan;
    private File root;
    private File sourceFile;
    private int nextNode = 0;
    private String code = "package Commands;\n" +
            "import static java.lang.Math.*; \n" +
            "import java.util.HashMap; \n" +
            "public class ActionList{" + " \n ";
    Class<?> actionList;
    Object instance;
    private boolean errors = false;


    public AlgorithmParser(GUI gui, String varSequence, String rules){
        this.gui = gui;
        this.varSequence = varSequence;
        scanner = new Scanner(rules);
    }

    public void fillVarTable(LinkedList<Integer> list){
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            varTable.put(String.valueOf(varSequence.charAt(index)),list.get(i));
            index+=2;
        }
    }

    public HashMap parse() {
        root = new File("src/Commands");
        sourceFile = new File(root, "/ActionList.java");
        sourceFile.getParentFile().mkdirs();
        scanner.useDelimiter(";");
        ruleSeq();
        return ruleTable;
        //TODO: Rensa upp allt efter ruleSeq()
    }

    /**
     * Scanner and creator of rules
     */
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

    /**
     * Start of rule creation
     * @return finished rule
     */
    private Rule rule() {
        Rule rule = new Rule(varTable, newSymbol);
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


    /**
     * Method that defines the "left side" which consists of
     * the Symbol(s) of the Rule and all parameters
     * @param rule the rule being written
     */
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
                errors = true;
                System.out.println("Symbol already exists");
            } else {
                rule.setTransitionSymbol(sym);
            }
        }
        else{
            errors = true;
            System.out.println("error, not valid symbol (not in table)");
        }
    }

    /**
     * Method which defines parameters in each rule
     * @param rule the rule that gets parameters
     */
    private void localSeq(Rule rule) {
        local(rule);
        if(currentScan.indexOf("]")==0){
            currentScan.deleteCharAt(0);
            if (currentScan.indexOf("[")==0){
                currentScan.deleteCharAt(0);
                localSeq(rule);
            }
            else if(currentScan.indexOf("->")!=0){
                errors = true;
                System.out.println("LocalSeq error");
            }
        }
        else{
            errors = true;
            System.out.println("localSeq error, exptecting ']'");
        }
    }

    /**
     * Definition of a parameter, checking it's validity etc.
     * @param rule The rule where the parameter is defined
     */
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
            errors = true;
            System.out.println("error, localVar variable error, not in table: " + token);
        }
    }

    /**
     * Method which collects all terms for a specific rule
     * @param rule the rule getting some terms
     * @return the collection of terms
     * @throws IOException
     * @throws ClassNotFoundException
     */
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
        errors = true;
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
                                errors = true;
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
            errors = true;
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
                    errors = true;
                    System.out.println("error queryError 1: ");
                }
                else{
                    currentScan.deleteCharAt(0);
                }
            } else {
                errors = true;
                System.out.println("queryExp error");
            }
            if (currentScan.indexOf("{") == 0) {
                currentScan.deleteCharAt(0);
                LinkedList elSelection = termExpSeq(rule);
                returner.add("else");
                returner.addAll(elSelection);
                String token = String.valueOf(currentScan.charAt(0));
                if (!token.equals("}")) {
                    errors = true;
                    System.out.println("error queryError 2: " + token);
                }
            }
        }
        return returner;
    }

    private LinkedList<String> symExp(Rule rule) {
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
    public boolean createActionListFile() throws IOException {
        code += "}";
        Files.write(sourceFile.toPath(),code.getBytes(StandardCharsets.UTF_8));
        return errors;
    }

    public String compileActionList(){

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int result = compiler.run(null,null,null,sourceFile.getPath());

        return null;
    }

    public String instanciateActionList() throws ClassNotFoundException, MalformedURLException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        actionList = Class.forName("Commands.ActionList", true, classLoader);
        try {
            instance = actionList.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void executeRule(Rule rule, String parent,LinkedList<Integer> parameters) {
        System.out.println("executing rule: " + rule.getTransitionSymbol()+rule.getParameters());
        String node = rule.getTransitionSymbol();
        LinkedList<String> executor = rule.getCode();
        if(parameters!=null){
            int indexer = 0;
            for (int parameter : parameters) {
                rule.setLocalVariable(rule.getParameters().get(indexer),parameter);
                node+=parameter;
                indexer++;
            }
            executor.add(0,node);
        }else{
            executor.add(0,node);
        }
        if (parent != null){
            gui.addNodeToTree(executor.get(0),nextNode, parent);
            nextNode++;
        }else{
            gui.addRootToTree(executor.get(0),nextNode);
            nextNode++;
        }
        HashMap<String, Integer> unmodifiedVariables = (HashMap<String, Integer>) rule.getLocalVariables().clone();
        int index = 1;
        while (index<executor.size()) {
            switch (executor.get(index)) {
                case "^":
                    index = executeLoop(index, executor, rule);
                    break;
                case "?":
                    index = executeQuery(index, executor, rule);
                    break;
                case "S":
                    index = executeSymbol(index, executor, rule);
                    break;
                case "else":
                    index = executor.size();
                    break;
                default:
                    if(index==0) {
                        executor.remove(index);
                    }else{
                        index++;
                    }
                    break;
            }
            rule.returnLocalVariables(unmodifiedVariables);
        }
        System.out.println("Exiting rule: " + rule.getTransitionSymbol() + rule.getParameters());
    }

    public int executeExpression(int symbol, HashMap<String,Integer> variables){
        try {
            instance = actionList.newInstance();
            Method method = actionList.getDeclaredMethod("activate"+symbol,HashMap.class);
            return (int) method.invoke(instance,variables);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private boolean executeBoolean(int symbol, HashMap<String, Integer> variables) {
        try {
            instance = actionList.newInstance();
            Method method = actionList.getDeclaredMethod("activate"+symbol,HashMap.class);
            return (boolean) method.invoke(instance,variables);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int executeQuery(int index, LinkedList<String> executor, Rule rule) {
        if(executeBoolean(Integer.parseInt(executor.get(index+1)),rule.getLocalVariables())){
            index+=2;
            switch (executor.get(index)){
                case "^":
                    index = executeLoop(index,executor,rule);
                    break;
                case "?":
                    index = executeQuery(index,executor,rule);
                    break;
                case "S":
                    index = executeSymbol(index,executor,rule);
                    break;
            }
        }else{
            while (!executor.get(index).equals("else")){
                index++;
            }
            index++;
            switch (executor.get(index)){
                case "^":
                    index = executeLoop(index,executor,rule);
                    break;
                case "?":
                    index = executeQuery(index,executor,rule);
                    break;
                case "S":
                    index = executeSymbol(index,executor,rule);
                    break;
            }
        }
        return index;
    }

    private int executeSymbol(int index, LinkedList<String> executor, Rule rule) {
        int limiter = index+1;
        index++;
        while (limiter < executor.size() && !executor.get(limiter).equals("S") && !executor.get(limiter).equals("else") && !executor.get(limiter).equals("^") && !executor.get(limiter).equals("?")){
            limiter++;
        }
        limiter--;
        LinkedList<Integer> parameters = null;
        if(limiter!=index) {
            parameters = new LinkedList<>();
            for (int i = index + 1; i <= limiter; i++) {
                parameters.add(executeExpression(Integer.parseInt(executor.get(i)), rule.getLocalVariables()));
            }

            System.out.println("execute symbol: " + executor.get(index).toString() + "," +parameters.toString() + " size: " + parameters.size());
        }
        if(ruleTable.get(executor.get(index))!=null) {
            String next = executor.get(0);
            executor.remove(0);
            index--;
            executeRule(ruleTable.get(executor.get(index)), next, parameters);
        }else{
            gui.addNodeToTree(executor.get(index),nextNode,executor.get(0));
            nextNode++;
            limiter++;
        }
        index = limiter;
        return index;
    }



    private int executeLoop(int index, LinkedList<String> executor, Rule rule) {

        int iterator;
        int top = executeExpression(Integer.parseInt(executor.get(index+3)),rule.getLocalVariables());
        index += 4;
        int outdex = 0;
        for (iterator = rule.getLocalVariables().put("i",executeExpression(Integer.parseInt(executor.get(index - 2)),rule.getLocalVariables())); iterator <= top; iterator++) {
            if(executor.get(0).equals("^")){
                executor.add(0,rule.getTransitionSymbol());
            }
            switch (executor.get(index)){
                case "^":
                    outdex = executeLoop(index,executor,rule);
                    break;
                case "?":
                    outdex = executeQuery(index,executor,rule);
                    break;
                case "S":
                    outdex = executeSymbol(index,executor,rule);
                    break;
            }

        }
        return outdex;
    }
}



