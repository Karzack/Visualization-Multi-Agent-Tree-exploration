package AgentNetwork;

import Tree.Node;
import Tree.Tree;

import java.util.HashMap;
import java.util.LinkedList;

public class AgentNetwork {
    private LinkedList<Agent> allAgents = new LinkedList<Agent>();
    private HashMap<Integer, Node> networkNodes = new HashMap<Integer, Node>();
    private Tree tree;

    public AgentNetwork(Tree tree){
        this.tree = tree;
    }

    public String requestLog(Agent agent){
        return agent.sendLog();
    }

    public void showLog(int id){
        String log = requestLog(allAgents.get(id));
        //TODO: Fill with updates on visual effects when looking at a specific agent.

    }
}
