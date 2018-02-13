package Tree;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Trad {
    private HashMap<Integer,Node> allNodes = new HashMap<Integer,Node>();
    private HashMap<String,Edge> allEdges = new HashMap<String,Edge>();


    public HashMap<Integer, Node> getAllNodes() {
        return allNodes;
    }

    public HashMap<String, Edge> getAllEdges() {
        return allEdges;
    }

    public Trad(){

    }

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
                    Node newNode = new Node(i);
                    Edge edge = currentNode.addChild(newNode);
                    allNodes.put(newNode.getId(),newNode);
                    allEdges.put(edge.getId(),edge);
                    notPlaced = false;
                }
                else{
                    currentNode = currentNode.getChildren().get(check).getChild();
                }
            }
        }

        //allEdges.put("1-0",new Edge(allNodes.get(1),allNodes.get(0)));
        printLog();
    }


    public void printLog(){
        for (Map.Entry<String,Edge> edges: allEdges.entrySet()) {
            System.out.println(edges.getValue().getId());
        }
    }
}
