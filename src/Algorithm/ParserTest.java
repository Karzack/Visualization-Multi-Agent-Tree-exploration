package Algorithm;

import java.util.HashMap;

public class ParserTest {
    public static void main(String[] args){
        HashMap<String,Rule> rules = new AlgorithmParser().parse();
        System.out.println("" + rules);
        rules.forEach((s, rule) ->{
            rule.toString();
        });
    }
}
