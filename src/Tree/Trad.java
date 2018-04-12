package Tree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class Trad {
    private HashMap<Integer,Node> allNodes = new HashMap<>();
    private HashMap<String,Integer> nodeTranslator = new HashMap<>();
    private LinkedList<Edge> allEdges = new LinkedList<>();
    private Node root;
    private int nextNode = 0;

    public HashMap<Integer, Node> getAllNodes() {
        return allNodes;
    }

    public LinkedList<Edge> getAllEdges() {
        return allEdges;
    }

    public Trad(){

    }

    /**
     * Method that builds the Trad with a number of nodes equal to amountOfNodes.
     * @param amountOfNodes
     */
    public void buildTree(int amountOfNodes){
        Node root = new Node(0);
        root.setName("" +0);
        allNodes.put(0,root);
        Random random = new Random();
        for (int i = 1; i < amountOfNodes; i++) {
            Node currentNode = root;
            boolean notPlaced=true;
            while (notPlaced){
                int check = random.nextInt(currentNode.getChildren().size()+1);
                if(check==currentNode.getChildren().size()){
                    Node node = new Node(i);
                    node.setName(""+i);
                    allNodes.put(node.getId(),node);

                    Edge edge = currentNode.addChild(node);
                    allEdges.add(edge);
                    notPlaced = false;
                }
                else{
                    currentNode = currentNode.getChildren().get(check).getChild();
                }
            }
        }
        printLog();
    }

    public void buildRoot(String id, int index){

        Node root = new Node(index);

        root.setName(id);
        allNodes.put(index,root);
        nodeTranslator.put(id,index);
        //nextNode++;
    }

    public void buildNode(String id, int index, String parent){
        Node node = new Node(index);
        node.setName(id);
        allNodes.put(index, node);
        nodeTranslator.put(node.getName(),index);
        Edge edge = allNodes.get(nodeTranslator.get(parent)).addChild(node);
        allEdges.add(edge);
        //nextNode++;
    }

    public Node getRoot(){
        return allNodes.get(0);
    }

    public int getTreeSize(){
        return allNodes.size();
    }


    public void printLog(){
        for (Edge edges: allEdges) {
            //System.out.println(edges.getValue().getId());
        }
    }
}
