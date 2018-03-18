package Commands;

public abstract class ActionInterface {

    public int activate(){
        System.out.println("class method has run");
        return 0;
    }
    public int activate(int a){
            return a;
    }
    public int activate(int a, int b){
        return a;
    }
    public int activate(int a, int b, int c){
        return a;
    }
    public int activate(int a, int b, int c, int d){
        return a;
    }
    public int activate(int a, int b, int c, int d, int e){
        return a;
    }
    public int activate(int a, int b, int c, int d, int e, int f){
        return a;
    }
    public int activate(int a, int b, int c, int d, int e, int f, int g){
        return a;
    }
    public int activate(int a, int b, int c, int d, int e, int f, int g, int h){
        return a;
    }

}
