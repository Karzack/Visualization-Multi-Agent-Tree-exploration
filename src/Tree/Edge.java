package Tree;

public class Edge {
    private Node parent;
    private Node child;
    private String id;

    public Edge(){

    }

    public Edge(Node parent, Node child){
        this.parent=parent;
        this.child=child;
        id = ""+parent.getId()+"-"+child.getId();
    }
    public Node getChild() {
        return child;
    }

    public Node getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }
}
