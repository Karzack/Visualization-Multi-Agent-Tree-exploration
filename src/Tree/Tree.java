package Tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tree {
    private HashMap<Integer,Node> allNodes = new HashMap<Integer,Node>();
    private HashMap<String,Edge> allEdges = new HashMap<String,Edge>();


    public Tree(){

    }

    /**
     * Method that builds the Tree with a number of nodes equal to amountOfNodes.
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
                    Edge edge = currentNode.addChild(new Node(i));
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


    public void printLog(){
        for (Map.Entry<String,Edge> edges: allEdges.entrySet()) {
            System.out.println(edges.getValue().getId());
        }
    }
}
