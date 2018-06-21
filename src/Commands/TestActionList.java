package Commands;
import static java.lang.Math.*; 
import java.util.HashMap; 
public class TestActionList{ 
    public int activate1(HashMap<String,Integer> vars){ 
        return 1; 
 } 
   public int activate2(HashMap<String,Integer> vars){ 
        return vars.get("k"); 
 } 
   public int activate3(HashMap<String,Integer> vars){ 
        return vars.get("t"); 
 } 
   public int activate4(HashMap<String,Integer> vars){ 
        return (int)floor(vars.get("i")/(log(vars.get("i"))+1)); 
 } 
   public boolean activate5(HashMap<String,Integer> vars){ 
        return vars.get("j")>0; 
 } 
   public int activate6(HashMap<String,Integer> vars){ 
        return 2; 
 } 
   public int activate7(HashMap<String,Integer> vars){ 
        return vars.get("t"); 
 } 
   public int activate8(HashMap<String,Integer> vars){ 
        return vars.get("j")-1; 
 } 
   public int activate9(HashMap<String,Integer> vars){ 
        return 1; 
 } 
   public int activate10(HashMap<String,Integer> vars){ 
        return vars.get("t"); 
 } 
   public boolean activate11(HashMap<String,Integer> vars){ 
        return vars.get("m")>0; 
 } 
   public int activate12(HashMap<String,Integer> vars){ 
        return vars.get("m")-1; 
 } 
   public int activate13(HashMap<String,Integer> vars){ 
        return vars.get("j"); 
 } 
   public int activate14(HashMap<String,Integer> vars){ 
        return vars.get("j"); 
 } 
}