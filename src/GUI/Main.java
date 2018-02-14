package GUI;

import AgentNetwork.AgentNetwork;
import Tree.Trad;

public class Main {

    public static void main(String[] args){
        Trad trad = new Trad();
        trad.buildTree(1000);
        AgentNetwork network = new AgentNetwork(trad, 10);

        while (trad.getTreeSize() != network.getTreeSize()){
            network.traverseExecute();

        }
        System.out.println("Tree with " + trad.getTreeSize() + " nodes explored in " + network.getTimestep() + " steps");
        network.getAllAgents().forEach(agent -> {
            System.out.println("Agent " + agent.getId() + " route: " + agent.sendLog());
        });
        System.exit(0);
    }
}
