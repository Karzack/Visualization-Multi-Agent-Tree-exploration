package AgentNetwork;

import Tree.Edge;
import Tree.Node;

import java.util.HashMap;
import java.util.Random;

import static AgentNetwork.TraverseActionEnum.DOWN;
import static AgentNetwork.TraverseActionEnum.STAY;
import static AgentNetwork.TraverseActionEnum.UP;

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
                return UP;
            }
            else{
                return TraverseActionEnum.STAY;
            }
        }
    }
    public static TraverseActionEnum determineByRandom(){
        switch(new Random().nextInt(3)){
            case 0:
                return UP;
            case 1:
                return DOWN;
            case 2:
                return STAY;
        }
        return STAY;
    }
}
