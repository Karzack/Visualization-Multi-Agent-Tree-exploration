package AgentsList;
import AgentNetwork.Agent; 
import AgentNetwork.AgentNetwork; 
import AgentNetwork.TraverseActionEnum; 
import Tree.Edge;
import Tree.Node;
import static java.lang.Math.*;
import java.util.HashMap; 
public class CAgent extends Agent{ 
 public CAgent(Node root, String id, AgentNetwork network){
        this.id=id;
        currentNode=root;
        this.network = network;
        log = network.getRoot().getName();
    }
@Override 
public void determinePath(){ 
Edge nextRoute = getCurrentNode().getChildren().stream().filter(edge -> !network.getCurrentTree().containsKey(edge.getChild().getId())).findFirst().orElse(null);
        if(nextRoute!=null){
            traverse(TraverseActionEnum.DOWN,currentNode.getChildren().indexOf(nextRoute));
        }
        else{
            if(getCurrentNode().getToParent()!=null) {
                traverse(TraverseActionEnum.UP,0);
            }
            else{
                traverse(TraverseActionEnum.STAY,0);
            }
        }
    }
}