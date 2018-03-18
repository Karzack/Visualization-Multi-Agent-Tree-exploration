package Algorithm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.LinkedList;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Rule {
    private String symbol = null;
    private LinkedList<String> parameterList = new LinkedList<>();
    private int noofParameters = 0;
    private final String varSequence;
    private HashMap<String, Integer> localTables;
    private String[] code;
    private String transitionSymbol = "";
    private NewSymbol newSymbol;

    public Rule(String varSequence, HashMap<String, Integer> varTable, NewSymbol newSymbol) {
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
            localTables.remove(token);
            parameterList.remove(token);
            noofParameters--;
            parameterList.add(token);
            noofParameters++;
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

    public LinkedList<Object> makeExpressionEvaluator(String exp) throws IOException, ClassNotFoundException {
        LinkedList<Object> returner = new LinkedList<>();
        String symbol = newSymbol.getNewSymbol();
        String parameters = "";
        String className = "Action" + symbol;
        File root = new File("src/Commands");
        File sourceFile = new File(root, "/Action" + symbol +".java");
        sourceFile.getParentFile().mkdirs();
        parameters += "int " + parameterList.get(0);
        for (int i = 1; i < parameterList.size(); i++){
            parameters += ",int " + parameterList.get(i);
        }
        String code = "package Commands;\n" +
                "public class " + className +  "{" + " \n " +
                "   public int activate" + symbol +  "(" /*+ parameters */+ "){ \n " +
                "System.out.println(\"This is Action" + symbol + "\"); \n" +
                "       return " + exp + "; \n " +
                "}" +
                "}";

        Files.write(sourceFile.toPath(),code.getBytes(StandardCharsets.UTF_8));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null,null,null,sourceFile.getPath());
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        Class<?> cls = Class.forName("Commands.Action"+symbol, true, classLoader);
        try {
            Object instance = cls.newInstance();
            Method method = cls.getMethod("activate"+symbol,String[].class);
            method.invoke(null);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        returner.add("Action"+symbol);
        return returner;
    }

    public LinkedList<Object> makeBooleanEvaluator(String exp) throws IOException, ClassNotFoundException {
        LinkedList<Object> returner = new LinkedList<>();
        String symbol = newSymbol.getNewSymbol();
        String parameters = "";
        String className = "Action" + symbol;
        File root = new File("src/Commands");
        File sourceFile = new File(root, "/Action" + symbol +".java");
        sourceFile.getParentFile().mkdirs();
        parameters += "int " + parameterList.get(0);
        for (int i = 1; i < parameterList.size(); i++){
            parameters += ",int " + parameterList.get(i);
        }
        String code = "package Commands;\n" +
                "public class " + className +  "{" + " \n " +
                "   public boolean activate" + symbol + "(" + parameters + "){ \n " +
                "System.out.println(\"This is Action" + symbol + "\"); \n" +
                "       return " + exp + "; \n " +
                "}" +
                "}";

        Files.write(sourceFile.toPath(),code.getBytes(StandardCharsets.UTF_8));
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null,null,null,sourceFile.getPath());
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
        Class<?> cls = Class.forName("Commands.Action"+symbol, true, classLoader);
        try {
            Object instance = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        returner.add("Action"+symbol);
        return returner;
    }
}

