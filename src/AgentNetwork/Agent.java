package AgentNetwork;

import Tree.Node;

public class Agent {
    private int id;
    private String log = "";
    private Node currentNode;


    public Agent(Node root, int id){
        this.id=id;
        currentNode=root;
    }

    public int getId() {
        return id;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public String sendLog() {
        return log;
    }

    public void registerLog(Node node){
        log = log +"," + node.getId() ;
    }

    public void traverse(){
        //TODO: Determine how an Agent moves from one node to another
    }
}
