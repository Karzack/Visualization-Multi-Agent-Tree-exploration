package GUI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class AgentCreator {
    private JTextArea txtAgentBehavior;
    private JButton btnAddBehavior;
    private JList<String> listAgents;
    private JPanel mainPanel;
    private JTextArea txtListOfAgents;
    private JTextField txtAgentName;
    private JButton btnAgentInstructions;
    private GUI mainGui;
    private ArrayList<String> agentList;
    private File agentTypeFile;


    public AgentCreator(GUI mainGui, ArrayList<String> agentList, File agentTypeFile) {
        this.agentList = agentList;
        this.agentTypeFile = agentTypeFile;
        for (String agent: agentList) {
            txtListOfAgents.append(agent + "\n");
        }
        this.mainGui = mainGui;
        btnAddBehavior.addActionListener(e -> {
            if(txtAgentName.getText()!=null && txtAgentBehavior.getText()!=null){
                try {
                    File root = new File("src/AgentsList");
                    File sourceFile = new File(root, "/"+ txtAgentName.getText() + "Agent.java");
                    sourceFile.getParentFile().mkdirs();
                    String code = "package AgentsList;\n" +
                            "import AgentNetwork.Agent; \n"+
                            "import AgentNetwork.AgentNetwork; \n"+
                            "import AgentNetwork.TraverseActionEnum; \n"+
                            "import Tree.Edge;\n" +
                            "import Tree.Node;\n" +
                            "import static java.lang.Math.*;\n"+
                            "import java.util.HashMap; \n"+
                            "public class "+txtAgentName.getText() + "Agent extends Agent{" + " \n " +
                            "public "+txtAgentName.getText() +"Agent(Node root, String id, AgentNetwork network){\n" +
                            "        this.id=id;\n" +
                            "        currentNode=root;\n" +
                            "        this.network = network;\n" +
                            "        log = network.getRoot().getName();\n" +
                            "    }\n"+
                            "@Override \n" +
                            "public void determinePath(){ \n" +
                            txtAgentBehavior.getText() + "\n" +
                            "}";
                    Files.write(sourceFile.toPath(),code.getBytes(StandardCharsets.UTF_8));
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    compiler.run(null, null, null, sourceFile.getPath());
                    if(!agentList.contains(txtAgentName.getText())){
                        agentList.add(txtAgentName.getText());
                        txtListOfAgents.append(txtAgentName.getText());
                        Files.write(agentTypeFile.toPath(),agentList);

                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        btnAgentInstructions.addActionListener(e -> {
            JFrame instructFrame = new JFrame("Agent Instruction");
            instructFrame.setContentPane(new AgentCreationInstruction().getPanel());
            instructFrame.pack();
            instructFrame.setResizable(false);
            instructFrame.setAlwaysOnTop(true);
            //instructFrame.setSize(740,500);
            instructFrame.setVisible(true);
            instructFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
