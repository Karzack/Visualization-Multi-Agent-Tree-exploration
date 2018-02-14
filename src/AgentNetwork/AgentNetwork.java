package AgentNetwork;

import Tree.Node;
import Tree.Trad;

import java.util.HashMap;
import java.util.LinkedList;

public class AgentNetwork {
    private LinkedList<Agent> allAgents = new LinkedList<Agent>();
    private HashMap<Integer, Node> currentTree = new HashMap<Integer, Node>();
    private Trad trad;
    private int timestep = 0;



    public AgentNetwork(Trad trad){
        this.trad = trad;
    }

    public AgentNetwork(Trad trad, int numberOfAgents){
        this.trad = trad;
        createAgents(numberOfAgents);
        currentTree.put(trad.getRoot().getId(), trad.getRoot());
    }

    private void createAgents(int numberOfAgents) {
        for (int i = 0; i < numberOfAgents; i++) {
            allAgents.add(new Agent(trad.getRoot(), i, this));
        }
    }

    public LinkedList<Agent> getAllAgents() {
        return allAgents;
    }

    public void traverseExecute(){
        timestep++;
        allAgents.forEach(agent -> {
            TraverseActionEnum actionEnum = DeterminePath.determinePathCalculation(currentTree,agent);
            agent.traverse(actionEnum);
        });
    }

    public boolean checkExploration(Tree.Edge currentNode){
        return currentTree.containsValue(currentNode.getChild());
    }

    public String requestLog(Agent agent){
        return agent.sendLog();
    }

    public int getTreeSize(){
        return currentTree.size();
    }

    public int getTimestep(){
        return timestep;
    }

    public String showLog(int id){
        return requestLog(allAgents.get(id));
        //TODO: Fill with updates on visual effects when looking at a specific agent.

    }

    public void markExplored(Node currentNode) {
        currentTree.put(currentNode.getId(),currentNode);
    }
}
