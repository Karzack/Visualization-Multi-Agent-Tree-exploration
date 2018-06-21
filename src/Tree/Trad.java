package Tree;

import java.util.HashMap;
import java.util.LinkedList;

public class Trad {
    private HashMap<Integer,Node> allNodes = new HashMap<>();
    private HashMap<String,Integer> nodeTranslator = new HashMap<>();
    private LinkedList<Edge> allEdges = new LinkedList<>();
    private Node root;

    public HashMap<Integer, Node> getAllNodes() {
        return allNodes;
    }

    public LinkedList<Edge> getAllEdges() {
        return allEdges;
    }

    public Trad(){

    }

    public void buildRoot(String id, int index){

        Node root = new Node(index);

        root.setName(id);
        allNodes.put(index,root);
        nodeTranslator.put(id,index);
        this.root = root;
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
        return root;
    }

    public int getTreeSize(){
        return allNodes.size();
    }


}
