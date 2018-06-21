package AgentNetwork;

import Tree.Edge;
import Tree.Node;



public abstract class Agent {
    public String id;
    protected String log;
    protected Node currentNode;
    public AgentNetwork network;



    protected Agent(){

    }

    public AgentNetwork getNetwork() {
        return network;
    }

    public String getId() {
        return id;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public String sendLog() {
        return log;
    }

    private void registerLog(Node node){
        log = log +"," + node.getName() ;
    }

    public void traverse(int action, int index){
        switch (action){
            case 1:
                traverse(TraverseActionEnum.UP,index);
                break;
            case 2:
                traverse(TraverseActionEnum.DOWN,index);
                break;
            case 3:
                traverse(TraverseActionEnum.STAY,index);
                break;
        }
    }

    public void traverse(TraverseActionEnum action, int index) {

        switch (action) {
            case UP:
                if(currentNode.getToParent() != null)
            {
                currentNode = currentNode.getToParent().getParent();
            }
            registerLog(currentNode);
            break;
            case DOWN:
                    currentNode = currentNode.getChildren().get(index).getChild();
                    if(!network.checkExploration(currentNode.getToParent())){
                        network.markExplored(currentNode);
                    }
                    registerLog(currentNode);
                break;
            case STAY:
                if(currentNode!=network.getRoot()) {
                    registerLog(currentNode);
                }
                break;
        }

    }

    /**
     * Method to check if any unexplored nodes exists amongst specified node's children
     * @param checkNode the node to be checked
     * @return the edge to the unexplored node
     */
    public Edge getUnexploredNode(Node checkNode){
        return checkNode.getChildren().stream().filter(edge -> !network.getCurrentTree().containsKey(edge.getChild().getId())).findFirst().orElse(null);
    }

    /**
     * Method to check if any unexplored nodes exists amongst the current node's children
     * @return the edge to the unexplored node
     */
    public Edge getUnexploredNode(){
        return getCurrentNode().getChildren().stream().filter(edge -> !network.getCurrentTree().containsKey(edge.getChild().getId())).findFirst().orElse(null);
    }

    public abstract void determinePath();

}
