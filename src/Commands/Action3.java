package Commands;
import java.lang.Math.*; 
import java.util.HashMap; 
public class Action3{ 
    public boolean activate(HashMap<String,Integer> vars){ 
 System.out.println("This is Action3" + vars.get("a")); 
       return vars.get("m")>0; 
 }}