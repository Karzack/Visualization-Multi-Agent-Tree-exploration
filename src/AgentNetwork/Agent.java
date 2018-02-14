package AgentNetwork;

import Tree.Edge;
import Tree.Node;


public class Agent {
    private int id;
    private String log = "0";
    private Node currentNode;
    private AgentNetwork network;
    private int numberOfAgentsHere=0;


    public Agent(Node root, int id, AgentNetwork network){
        this.id=id;
        currentNode=root;
        this.network = network;
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

    public void traverse(TraverseActionEnum action) {
        switch (action) {
            case UP:
                currentNode = currentNode.getToParent().getParent();
                registerLog(currentNode);
                break;
            case DOWN:
                break;
            case EXPLORED:
                break;
            case STAY:
                registerLog(currentNode);
                break;
            case UNEXPLORED:
                Edge nextNode = getCurrentNode().getChildren().stream().filter(edge -> !network.checkExploration(edge)).findFirst().orElse(null);
                //currentNode.getChildren().forEach((edge -> {
                //  if(!network.checkExploration(edge)){
                currentNode = nextNode.getChild();
                registerLog(currentNode);
                network.markExplored(currentNode);
                break;
        }
        //}));
               /* Edge nextRoute = currentNode.getChildren().stream().filter(edge -> !network.checkExploration(edge)).findFirst().orElse(null);
                if(nextRoute!=null){
                    currentNode = nextRoute.getChild();
                    registerLog(currentNode);
                    network.markExplored(currentNode);
                }*/
    }


    public int getNumberOfAgentsHere() {
        return numberOfAgentsHere;
    }

    public void setNumberOfAgentsHere(int numberOfAgentsHere) {
        this.numberOfAgentsHere = numberOfAgentsHere;
    }
}
