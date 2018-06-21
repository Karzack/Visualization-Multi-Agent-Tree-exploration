package GUI;

import AgentNetwork.Agent;
import AgentNetwork.AgentNetwork;
import Tree.Node;

import javax.swing.*;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

class AgentcreationThread extends Thread {

    private String name;
    private String agentAmountText;
    private File root;
    private AgentNetwork agentNetwork;

    public AgentcreationThread(String name, String agentAmountText, File root, AgentNetwork agentNetwork){
        this.name = name;
        this.agentAmountText = agentAmountText;
        this.root = root;
        this.agentNetwork = agentNetwork;
    }

    @Override
    public void run() {
        try {
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{root.toURI().toURL()});
            Class<?> newAgentType = Class.forName("AgentsList." + name + "Agent", true, classLoader);
            Class[] cArgs = new Class[3];
            cArgs[0]= Node.class;
            cArgs[1]= String.class;
            cArgs[2]= AgentNetwork.class;
            int amount = Integer.parseInt(agentAmountText);
            for (int i = 0; i < amount; i++) {
                Agent agent = (Agent) newAgentType.getDeclaredConstructor(cArgs).newInstance(agentNetwork.getRoot(),name+i,agentNetwork);
                agentNetwork.createAgent(agent);
            }
        } catch (IllegalAccessException | IOException | InstantiationException | NoSuchMethodException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null,"Agents Created");
    }
}
