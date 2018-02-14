package Tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Trad {
    private HashMap<Integer,Node> allNodes = new HashMap<Integer,Node>();
    private HashMap<String,Edge> allEdges = new HashMap<String,Edge>();


    public Trad(){

    }

    /**
     * Method that builds the Trad with a number of nodes equal to amountOfNodes.
     * @param amountOfNodes
     */
    public void buildTree(int amountOfNodes){
        Node root = new Node(0);
        allNodes.put(0,root);
        Random random = new Random();
        for (int i = 1; i < amountOfNodes; i++) {
            Node currentNode = root;
            boolean notPlaced=true;
            while (notPlaced){
                int check = random.nextInt(currentNode.getChildren().size()+1);
                if(check==currentNode.getChildren().size()){
                    Node node = new Node(i);
                    allNodes.put(node.getId(),node);
                    Edge edge = currentNode.addChild(node);
                    allEdges.put(edge.getId(),edge);
                    notPlaced = false;
                }
                else{
                    currentNode = currentNode.getChildren().get(check).getChild();
                }
            }
        }
        printLog();
    }

    public Node getRoot(){
        return allNodes.get(0);
    }

    public int getTreeSize(){
        return allNodes.size();
    }


    public void printLog(){
        for (Map.Entry<String,Edge> edges: allEdges.entrySet()) {
            //System.out.println(edges.getValue().getId());
        }
    }
}
