package GUI;

import AgentNetwork.AgentNetwork;
import AgentNetwork.Agent;
import Rule.*;
import Tree.Edge;
import Tree.Node;
import Tree.Trad;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class GUI extends JPanel {
    private final File treeFile;
    private final File agentTypeFile;
    private final File rootStrorage;
    private LinkedList<Integer> variables;
    private JPanel mainPanel;
    private JTabbedPane mapsPane;
    private JPanel maps;
    private JPanel agentsMap;
    private JTextField showRouteTxt;
    private JPanel agentRoutePane;
    private JTextField txtVariables;
    private JTextField txtRoot;
    private JButton btnCreateRules;
    private JTextArea txtRulesAlgorithm;
    private JButton btnDrawAgents;
    private JCheckBox timedSecCheckBox;
    private JButton btnTraverse;
    private JTextField txtTime;
    private JButton btnShowRoute;
    private JButton resetButton;
    private JButton btnCompile;
    private JButton btnInstance;
    private JButton btnDrawNodes;
    private JLabel fastestAgent;
    private JLabel slowestAgent;
    private JLabel agentTimeStepCounter;
    private int agentTimeStepCounterNumber = 0;
    private Trad trad;
    private AgentNetwork agentNetwork;
    private HashMap<String, Rule> rulesTable;
    private JCheckBox showNodeLabelsCheckBox;
    private JButton btnInstruct;
    private JButton btnGetLog;
    private JButton generateTreeButton;
    private JButton removeRuleButton;
    private JPanel pnlListOfTrees;
    private JTextField txtTreeName;
    private JTextField txtVariableValues;
    private JButton addAgentButton;
    private JButton removeAgentButton;
    private JButton destroyAgentButton;
    private JButton singleStepTraverseButton;

    private RuleParser ruleParser;
    private VisualizationViewer<String, String> vv3;
    private ArrayList<String> agentTypeList;
    private ArrayList<String> ruleList;
    private ArrayList<String> treeList;
    private final File ruleFile;
    private TimedTraversal timedTraversalThread;


    public GUI() {
        rootStrorage = new File("src/Storage");
        ruleFile = new File(rootStrorage, "/Rules.txt");
        ruleFile.getParentFile().mkdirs();
        agentTypeFile = new File(rootStrorage,"/AgentTypes.txt");
        agentTypeFile.getParentFile().mkdirs();
        treeFile = new File(rootStrorage,"/TreeList.txt");
        treeFile.getParentFile().mkdirs();


        try {
            ruleList = (ArrayList<String>) Files.readAllLines(ruleFile.toPath());
            treeList = (ArrayList<String>) Files.readAllLines(treeFile.toPath());
            agentTypeList = (ArrayList<String>) Files.readAllLines(agentTypeFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pnlListOfTrees.setLayout(new BoxLayout(pnlListOfTrees,BoxLayout.Y_AXIS));
        for (int i = 0; i< treeList.size();i+=2){

            pnlListOfTrees.add(new JLabel("" + treeList.get(i)));
        }
        // Transformer maps the vertex number to a vertex property
        Transformer<Integer, Paint> vertexColor = i -> {
            if (i == 0) return Color.GREEN;
            return Color.RED;
        };
        Transformer<String, Paint> edgesColor = i -> {
            if (i.equals("")) return Color.GREEN;
            return Color.RED;
        };

        Transformer<Integer, Shape> vertexSize = (Integer i) -> (Ellipse2D) new Ellipse2D.Double(0, 0, 8, 8);

        btnCreateRules.addActionListener((ActionEvent e) -> {
            JFrame ruleFrame = new JFrame("Rule Creation");
            ruleFrame.setContentPane(new CreateRule(this).getMainPanel());
            ruleFrame.pack();
            ruleFrame.setResizable(false);
            ruleFrame.setAlwaysOnTop(true);
            ruleFrame.setSize(300,100);
            ruleFrame.setVisible(true);
            ruleFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        });


        btnGetLog.addActionListener((ActionEvent e) ->{
            JOptionPane.showMessageDialog(null, agentNetwork.showLog(0));
        });

        btnShowRoute.addActionListener((ActionEvent e) -> {
            agentRoutePane.removeAll();
            mapsPane.setSelectedIndex(2);
            //nodeTxt = nodes.getText();

            Agent agent = agentNetwork.getAgent(showRouteTxt.getText());
            String[] log = agent.sendLog().split(",");
            Graph<String, String> g = new DelegateForest<>();
            if (agent != null) {
                if(!timedSecCheckBox.isSelected()) {
                    mapsPane.setSelectedIndex(2);
                    String lastChar = log[0];
                    g.addVertex(lastChar);
                    for (int i = 1; i < log.length; i++) {
                        String logged = log[i];
                        if (!lastChar.equals(logged)) {
                            if (!g.containsVertex(logged)) {
                                g.addVertex(logged);

                            }

                        }
                        lastChar = logged;
                    }
                    for (int j = 1; j < log.length; j++) {
                        if (!g.containsEdge(log[j - 1] + "-" + log[j])
                                && !g.containsEdge(log[j] + "-" + log[j - 1])) {
                            g.addEdge(log[j - 1] + "-" + log[j], log[j - 1], log[j]);
                        }
                    }


                    Layout<String, String> layout3 = new RadialTreeLayout<>((Forest<String, String>) g);
                    vv3 = new VisualizationViewer<>(layout3);

                    final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
                    vv3.setGraphMouse(graphMouse3);
                    graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

                    Transformer<String, String> transformer = String::valueOf;
                    // Transformer maps the vertex number to a vertex property
                    Transformer<String, Paint> vertexColor1 = i -> {
                        if (i.equals("0")) return Color.GREEN;
                        return Color.RED;
                    };
                    Transformer<String, Paint> edgesColor1 = i -> {
                        if (i.equals("")) return Color.GREEN;
                        return Color.RED;
                    };

                    Transformer<String, Shape> vertexSize1 = i -> (Ellipse2D) new Ellipse2D.Double(-4, -4, 8, 8);

                    if(showNodeLabelsCheckBox.isSelected()) {
                        vv3.getRenderContext().setVertexLabelTransformer(transformer);
                    }
                    vv3.getRenderContext().setVertexShapeTransformer(vertexSize1);
                    //vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
                    vv3.getRenderContext().setVertexFillPaintTransformer(vertexColor1);
                    vv3.getRenderContext().setEdgeDrawPaintTransformer(edgesColor1);

                    agentRoutePane.add(vv3);
                    agentRoutePane.requestFocus();
                    agentRoutePane.updateUI();
                }
                else{
                    int time = Integer.parseInt(txtTime.getText());
                    new TimedTraversalSingle(agent,agentRoutePane,mapsPane,time,showNodeLabelsCheckBox.isSelected()).start();
                }

            }else {

            }
        });

        resetButton.addActionListener(e -> {
            maps.removeAll();
            agentsMap.removeAll();
            agentRoutePane.removeAll();
            mapsPane.updateUI();
            this.trad=null;
            this.agentNetwork=null;
            txtTime.setText("0");
            agentTimeStepCounterNumber=0;
            timedTraversalThread = null;
        });

        btnDrawAgents.addActionListener(e -> {
            JFrame agentFrame = new JFrame("Agent Creation");
            agentFrame.setContentPane(new AgentCreator(this,agentTypeList,agentTypeFile).getMainPanel());
            agentFrame.pack();
            agentFrame.setResizable(false);
            agentFrame.setAlwaysOnTop(true);
            agentFrame.setVisible(true);
            agentFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        });

        btnTraverse.addActionListener((ActionEvent e) -> {
            if(timedTraversalThread == null || !timedTraversalThread.isAlive()){
                int time = Integer.parseInt(txtTime.getText());
                timedTraversalThread = new TimedTraversal(trad, agentNetwork, mapsPane, agentsMap,time,fastestAgent,slowestAgent, agentTimeStepCounter,agentTimeStepCounterNumber,showNodeLabelsCheckBox.isSelected());
                timedTraversalThread.start();
                btnTraverse.setText("Stop Timed Traversal");
            }else if(timedTraversalThread.isAlive()){
                timedTraversalThread.stopWriting();
                btnTraverse.setText("Start Timed Traversal");
            }
        });

        btnCompile.addActionListener(e -> {
            ruleParser.compileActionList();
            JOptionPane.showMessageDialog(null,"Compiling complete!\nRestart application to use this tree");
        });

        /*btnInstance.addActionListener(e -> {
            try {
                ruleParser.instanciateActionList();
                JOptionPane.showMessageDialog(null,"Tree is ready to be drawn!");
                btnDrawNodes.setEnabled(true);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null,"No Class found!");
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null,"URL is not correct!");
            }
        });*/

        btnDrawNodes.addActionListener(e -> {
            String rules="";
            for (int i = 0; i < treeList.size(); i += 2) {
                if (treeList.get(i).equals(txtTreeName.getText())) {
                    rules = treeList.get(i + 1);
                }
            }
            ruleParser = new RuleParser(this, txtTreeName.getText(), rules);
            ruleParser.fillVarTable(txtVariables.getText(),txtVariableValues.getText());
            rulesTable = ruleParser.parse();
            try {
                ruleParser.instanciateActionList(txtTreeName.getText());
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            mapsPane.setSelectedIndex(0);
            DirectedGraph<String, String> g = new DelegateForest<>();
            trad = new Trad();


            Rule root = rulesTable.get(txtRoot.getText());
            ruleParser.executeRule(root, null, null);
            Node rootNode = trad.getRoot();
            g.addVertex(rootNode.getName());
            for (Edge edges : trad.getAllEdges()) {
                Node parent = edges.getParent();
                Node child = edges.getChild();
                int copy = 1;
                if(!g.containsVertex(child.getName())){
                    g.addVertex(child.getName());
                }else{
                    boolean noMoreCopies = true;
                    while(noMoreCopies) {
                        if (!g.containsVertex(child.getName()+"("+copy+")")) {
                            child.setName(child.getName() + "(" + copy + ")");
                            g.addVertex(child.getName());
                            noMoreCopies = false;
                        }else{
                            copy++;
                        }
                    }
                }
                g.addEdge(edges.getId(), parent.getName(), child.getName());
            }
            agentNetwork = new AgentNetwork(trad);
            Layout<String, String> layout3 = new RadialTreeLayout<>((Forest<String, String>) g);
            vv3 = new VisualizationViewer<>(layout3);

            final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
            vv3.setGraphMouse(graphMouse3);
            graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

            Transformer<String, String> transformer = String::valueOf;
            // Transformer maps the vertex number to a vertex property
            Transformer<String, Paint> vertexColor1 = i -> {
                if (i.equals("0") || i.equals(txtRoot.getText())) return Color.GREEN;
                return Color.RED;
            };
            Transformer<String, Paint> edgesColor1 = i -> {
                if (i.equals("")) return Color.GREEN;
                return Color.RED;
            };

            Transformer<String, Shape> vertexSize1 = i -> (Ellipse2D) new Ellipse2D.Double(-4, -4, 8, 8);
            if(showNodeLabelsCheckBox.isSelected()) {
                vv3.getRenderContext().setVertexLabelTransformer(transformer);
            }
            vv3.getRenderContext().setVertexShapeTransformer(vertexSize1);
            //vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
            vv3.getRenderContext().setVertexFillPaintTransformer(vertexColor1);
            vv3.getRenderContext().setEdgeDrawPaintTransformer(edgesColor1);
            maps.removeAll();
            maps.add(vv3);
            maps.requestFocus();
            maps.updateUI();

        });

        generateTreeButton.addActionListener(e -> {
            GenerateTree dialog = new GenerateTree(this);
            dialog.pack();
            dialog.setVisible(true);
        });
        removeRuleButton.addActionListener(e -> {
            RemoveRule dialog = new RemoveRule(this);
            dialog.pack();
            dialog.setVisible(true);
        });
        singleStepTraverseButton.addActionListener(e -> {
            if(timedTraversalThread!=null) {
                if (timedTraversalThread.isAlive()) {
                    timedTraversalThread.stopWriting();
                    btnTraverse.setText("Start Timed Traversal");
                }
            }
            HashMap<String, Node> tempTree = (HashMap<String, Node>) agentNetwork.getCurrentTree().clone();
            agentNetwork.traverseExecute();
            Graph<String, String> g = new DelegateForest<>();
            mapsPane.setSelectedIndex(1);

            for (Map.Entry<Integer, Node> nodes : agentNetwork.getCurrentTree().entrySet()) {

                g.addVertex(nodes.getValue().getName());

            }

            for (Edge edges : trad.getAllEdges()) {
                if (agentNetwork.getCurrentTree().containsValue(edges.getChild())) {
                    g.addEdge(edges.getId(), edges.getParent().getName(), edges.getChild().getName());
                }
            }

            Layout<String, String> layout3 = new RadialTreeLayout<>((Forest<String, String>) g);
            vv3 = new VisualizationViewer<>(layout3);

            final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
            vv3.setGraphMouse(graphMouse3);
            graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

            Transformer<String, String> transformer = String::valueOf;
            // Transformer maps the vertex number to a vertex property
            Transformer<String, Paint> vertexColorNodes = i -> {
                if (agentNetwork.getAllAgentLocations().get(i) > 0) return Color.CYAN;
                else if (i.equals("0") || i.equals(txtRoot.getText())) return Color.GREEN;
                return Color.RED;
            };

            Transformer<String, Paint> edgesColorEdges = i -> Color.RED;

            Transformer<String, Shape> vertexSize1 = i -> (Ellipse2D) new Ellipse2D.Double(-4, -4, 8, 8);

            if (showNodeLabelsCheckBox.isSelected()) {
                vv3.getRenderContext().setVertexLabelTransformer(transformer);
            }
            vv3.getRenderContext().setVertexShapeTransformer(vertexSize1);
            //vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
            vv3.getRenderContext().setVertexFillPaintTransformer(vertexColorNodes);
            vv3.getRenderContext().setEdgeDrawPaintTransformer(edgesColorEdges);
            agentsMap.removeAll();
            agentsMap.add(vv3);
            agentsMap.requestFocus();
            agentsMap.updateUI();
            agentTimeStepCounterNumber++;
            agentTimeStepCounter.setText("Step number: " + agentTimeStepCounterNumber);
            if (agentNetwork.checkIfAllAgentsInRoot() && agentNetwork.checkIfComplete()) {
                String[] fast = agentNetwork.lookForFastestAgent();
                String[] slow = agentNetwork.lookForSlowestAgent();
                fastestAgent.setText("Fastest Agent: " + fast[0] + " time: " + fast[1]);
                slowestAgent.setText("Slowest Agent" + slow[0] + " time: " + slow[1]);
            }
        });
        addAgentButton.addActionListener(e -> {
            AddAgentDialog dialog = new AddAgentDialog(this,agentNetwork,agentTypeList,new File("src/AgentsList"));
            dialog.pack();
            dialog.setVisible(true);
        });
    }

        private LinkedList<Integer> fillVariables(String variables) {
        LinkedList<Integer> inputs = new LinkedList<>();
        for(int i = 0; i<variables.length(); i+=2){
            inputs.add(Integer.parseInt(JOptionPane.showInputDialog(this, "Set value of variable " + variables.charAt(i) + ":","Input variable " + variables.charAt(i), JOptionPane.PLAIN_MESSAGE)));
        }
        return inputs;
    }


    public JPanel getPanel(){
        return mainPanel;
    }

    public void addNodeToTree(String child,int index, String parent){
        trad.buildNode(child,index,parent);
    }

    public void addRootToTree(String root, int index) {
        trad.buildRoot(root, index);
    }

    public ArrayList<String> getRuleList() {
        return ruleList;
    }

    public void writeNewRuleList(ArrayList<String> toFile) {
        try {
            Files.write(ruleFile.toPath(),toFile);
            ruleList = toFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRuleList(List<String> ruleList) {
        this.ruleList = (ArrayList<String>) ruleList;
    }

    public void generateTree(String treeName,String ruleList){
        ruleParser = new RuleParser(this,treeName,ruleList);
        try {
            rulesTable = ruleParser.parse();
            boolean error = ruleParser.createActionListFile();
            if(error){
                JOptionPane.showMessageDialog(null,"Rules has NOT been created, fix all errors before continuing");
            }else {
                JOptionPane.showMessageDialog(null, "Tree has been generated. Restart application to use this tree.");
                ruleParser.compileActionList();
                List<String> list = Files.readAllLines(treeFile.toPath(),StandardCharsets.UTF_8);
                if(!list.contains(treeName)) {
                    list.add(treeName);
                    list.add(ruleList);
                    Files.write(treeFile.toPath(), list);
                }else{
                    int index = list.indexOf(treeName);
                    list.set(index+1, ruleList);
                    Files.write(treeFile.toPath(), list);
                }
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}


