package Algorithm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParserTest {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        HashMap<String,Rule> rules = new AlgorithmParser().parse();

        System.out.println("" + rules);
        rules.forEach((s, rule) -> rule.toString());
    }
}
