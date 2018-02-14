package AgentNetwork;

import Tree.Edge;
import Tree.Node;

import java.util.HashMap;

public final class DeterminePath {

    public static TraverseActionEnum determinePathCalculation(HashMap<Integer, Node> currentTree, Agent agent){
        final boolean[] nodeFullyExplored = {true};
        Edge nextRoute = agent.getCurrentNode().getChildren().stream().filter(edge -> !currentTree.containsKey(edge.getChild().getId())).findFirst().orElse(null);
        if(nextRoute!=null){
            nodeFullyExplored[0]=false;
        /*agent.getCurrentNode().getChildren().forEach(edge -> {
            if(!currentTree.containsKey(edge.getChild().getId())){
                nodeFullyExplored[0] = false;
            }
        });
        if(!nodeFullyExplored[0]){*/
            return TraverseActionEnum.UNEXPLORED;
        }
        else{
            if(agent.getCurrentNode().getToParent()!=null) {
                return TraverseActionEnum.UP;
            }
            else{
                return TraverseActionEnum.STAY;
            }
        }
    }
}
