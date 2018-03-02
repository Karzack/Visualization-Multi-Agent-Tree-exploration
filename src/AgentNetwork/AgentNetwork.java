package AgentNetwork;

import Tree.Node;
import Tree.Trad;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AgentNetwork {
    private LinkedList<Agent> allAgents = new LinkedList<>();
    private HashMap<Integer, Node> currentTree = new HashMap<>();
    private LinkedList<Integer> containsAgents = new LinkedList<>();
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

    public LinkedList<Integer> getAllAgentLocations(){
        return containsAgents;
    }

    public LinkedList<Agent> getAllAgents() {
        return allAgents;
    }

    public void traverseExecute(){
        timestep++;
        allAgents.forEach(agent -> {
            TraverseActionEnum actionEnum = DeterminePath.determinePathCalculation(currentTree,agent);
            //TraverseActionEnum actionEnum = DeterminePath.determineByRandom();
            agent.traverse(actionEnum);
        });
        updateAgentLocation();
    }

    public HashMap<Integer, Node> getCurrentTree() {
        return currentTree;
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

    public void updateAgentLocation(){
        containsAgents.clear();
        for (Agent agent: allAgents){
            if(!containsAgents.contains(agent.getCurrentNode().getId())) {
                containsAgents.add(agent.getCurrentNode().getId());
            }
        }
    }

    public boolean checkIfComplete(){
        if(currentTree.size()==trad.getTreeSize()){
            return true;
        }
        else{
            return false;
        }
    }

    public int[] lookForFastestAgent(){
        Agent fastestAgent = null;
        int fastStep = Integer.MAX_VALUE;
        for(Agent fastAgent : allAgents){
            String[] time = fastAgent.sendLog().split(",");
            int stepper = 1;
            for(int i = 1;i<time.length;i++){
                if(time[i].equals(time[i-1]) && time[i].equals("0")){
                    stepper--;
                }
                else{
                    stepper++;
                }
            }
            if( stepper < fastStep){
                fastestAgent = fastAgent;
                fastStep = stepper;
            }
        }
        int[] returner = {fastestAgent.getId(),fastStep};
        return returner;
    }

    public int[] lookForSlowestAgent(){
        Agent slowestAgent = null;
        int slowStep = 0;
        for(Agent slowAgent : allAgents){
            String[] time = slowAgent.sendLog().split(",");
            int stepper = 1;
            for(int i = 1;i<time.length;i++){
                if(time[i].equals(time[i-1]) && time[i].equals("0")){
                    stepper--;
                }else{
                    stepper++;
                }
            }
            if( stepper > slowStep){
                slowestAgent = slowAgent;
                slowStep = stepper;
            }
        }
        int[] returner = {slowestAgent.getId(),slowStep};
        return returner;
    }

    public boolean checkIfAllAgentsInRoot() {
        if(containsAgents.size()==1 && containsAgents.getFirst()==0){
            return true;
        }
        return false;
    }
}

