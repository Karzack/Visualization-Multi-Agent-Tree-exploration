package Tree;

import java.util.LinkedList;

public class Node {
    private int id;
    private Edge toParent = null;
    private LinkedList<Edge> children = new LinkedList<>();
    private String Name;

    public Node(int id){
        this.id=id;
    }

    public LinkedList<Edge> getChildren() {
        return children;
    }

    public Edge addChild(Node child){
        Edge edge = new Edge(this,child);
        child.setToParent(edge);
        children.add(edge);
        return edge;
    }

    public Edge getToParent() {
        return toParent;
    }

    public void setToParent(Edge toParent) {
        this.toParent = toParent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
