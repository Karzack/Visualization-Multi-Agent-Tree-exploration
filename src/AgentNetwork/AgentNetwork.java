package AgentNetwork;

import Tree.Edge;
import Tree.Node;
import Tree.Trad;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class AgentNetwork {
    private LinkedList<Agent> allAgents = new LinkedList<>();
    private ArrayList<String> agentTypes = new ArrayList<>();
    private HashMap<Integer, Node> currentTree = new HashMap<>();
    private HashMap<String,Integer> containsAgents = new HashMap<>();
    private Trad trad;
    private int timestep = 0;
    File root = new File("src/AgentsList");



    public AgentNetwork(Trad trad){
        this.trad = trad;
        currentTree.put(trad.getRoot().getId(), trad.getRoot());
        for(Map.Entry<Integer, Node> nodes : trad.getAllNodes().entrySet()){
            containsAgents.put(nodes.getValue().getName(),0);
        }
    }

    public void createAgent( Agent agent) {
        allAgents.add(agent);
        if(containsAgents.get(trad.getRoot().getName())!=null) {
            int update = containsAgents.get(trad.getRoot().getName());
            containsAgents.put(trad.getRoot().getName(),update+1);
        }else {
            containsAgents.put(trad.getRoot().getName(), 1);
        }
    }

    public HashMap<String,Integer> getAllAgentLocations(){
        return containsAgents;
    }

    public LinkedList<Agent> getAllAgents() {
        return allAgents;
    }

    public void traverseExecute(){
        timestep++;
        allAgents.forEach(agent -> {
            containsAgents.put(agent.getCurrentNode().getName(),containsAgents.get(agent.getCurrentNode().getName())-1);
            agent.determinePath();
            containsAgents.put(agent.getCurrentNode().getName(), containsAgents.get(agent.getCurrentNode().getName()) + 1);
        });
    }

    public boolean checkExploration(Tree.Edge currentNode){
        return currentTree.containsValue(currentNode.getChild());
    }

    private String requestLog(Agent agent){
        return agent.sendLog();
    }

    public int getAmountOfAgentsInNode(Node node){

        return containsAgents.get(node.getName());
    }

    public HashMap<Integer, Node> getCurrentTree() {
        return currentTree;
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

    public boolean checkIfExplored(Node node){
        boolean explored = false;
        for (Edge edge: node.getChildren()) {
            if(currentTree.containsValue(edge.getChild())){
                explored=true;
            }
        }
        return !explored;
    }

    public boolean checkIfComplete(){
        return currentTree.size() == trad.getTreeSize();
    }

    public String[] lookForFastestAgent(){
        Agent fastestAgent = null;
        int fastStep = Integer.MAX_VALUE;
        for(Agent fastAgent : allAgents){
            String[] time = fastAgent.sendLog().split(",");
            int stepper = 1;
            for(int i = 1;i<time.length;i++){
                    stepper++;
            }
            if( stepper < fastStep){
                fastestAgent = fastAgent;
                fastStep = stepper -1;
            }
        }
        return new String[]{fastestAgent.getId(),""+fastStep};
    }

    public String[] lookForSlowestAgent(){
        Agent slowestAgent = null;
        int slowStep = 0;
        for(Agent slowAgent : allAgents){
            String[] time = slowAgent.sendLog().split(",");
            int stepper = 1;
            for(int i = 1;i<time.length;i++){
                    stepper++;
            }
            if( stepper > slowStep){
                slowestAgent = slowAgent;
                slowStep = stepper-1;
            }
        }
        return new String[]{slowestAgent.getId(),""+slowStep};
    }

    public boolean checkIfAllAgentsInRoot() {
        return containsAgents.get(getRoot().getName()).equals(allAgents.size());
    }

    public Node getRoot() {
        return trad.getRoot();
    }

    public Agent getAgent(String id) {
        for (Agent agent: allAgents) {
            if(agent.getId().equals(id)){
                return agent;
            }
        }
        return null;
    }

    public ArrayList<String> getAgentTypes() {
        return agentTypes;
    }

    public void addAgentType(String type){
        agentTypes.add(type);
    }

    public void removeAllAgentsOfType(String text) {
        int amount = 0;
        for(int i = allAgents.size()-1; i>=0;i--){
            if (allAgents.get(i).getId().charAt(0)==text.charAt(0)){
                allAgents.remove(i);
                amount++;
            }
        }
        containsAgents.put(trad.getRoot().getName(),containsAgents.get(trad.getRoot().getName())-amount);
    }
}

