package GUI;

import AgentNetwork.AgentNetwork;
import AgentNetwork.Agent;
import Algorithm.AlgorithmParser;
import Algorithm.Rule;
import Tree.Edge;
import Tree.Node;
import Tree.Trad;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Transformer;
import org.omg.SendingContext.RunTime;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.List;

public class GUI extends JPanel {
    private String nodeTxt;
    private LinkedList<Integer> variables;
    private JPanel mainPanel;
    private JTextField nodes;
    private JButton drawBtn;
    private JTabbedPane mapsPane;
    private JPanel maps;
    private JPanel agentsMap;
    private JTextField showRouteTxt;
    private JPanel agentRoutePane;
    private JTextField txtVariables;
    private JTextField txtRoot;
    private JButton btnCreateRules;
    private JTextArea txtRulesAlgorithm;
    private JTextField txtAgents;
    private JButton btnDrawAgents;
    private JCheckBox timedSecCheckBox;
    private JButton btnTraverse;
    private JTextField txtTime;
    private JButton btnShowRoute;
    private JButton resetButton;
    private JButton btnCompile;
    private JButton btnInstance;
    private JButton btnDrawNodes;
    private JCheckBox timedTraversalCheckBox;
    private JTextField txtTImeInterval;
    private JLabel fastestAgent;
    private JLabel slowestAgent;
    private JLabel agentTimeStepCounter;
    private int agentTimeStepCounterNumber = 0;
    private Trad trad;
    private AgentNetwork agentNetwork;
    private HashMap<String, Rule> rulesTable;
    private JLabel lblRule;
    private JLabel lblAgents;
    private JCheckBox checkBoxSimpleDraw;
    private JTextField txtSimpleDraw;

    private AlgorithmParser algorithmParser;
    private VisualizationViewer<String, String> vv3;


    public GUI() {

        // Transformer maps the vertex number to a vertex property
        Transformer<Integer, Paint> vertexColor = i -> {
            if (i == 0) return Color.GREEN;
            return Color.RED;
        };
        Transformer<String, Paint> edgesColor = i -> {
            if (i == "") return Color.GREEN;
            return Color.RED;
        };

        Transformer<Integer, Shape> vertexSize = i -> {
            Ellipse2D circle = new Ellipse2D.Double(0, 0, 8, 8);
            return circle;
        };

        btnCreateRules.addActionListener(e -> {
            if(txtVariables.getText().length()==0 || txtRoot.getText().length()==0 || txtRulesAlgorithm.getText().length()==0)
            {
                JOptionPane.showMessageDialog(this, "You must have a variable, a root node and rules to draw anything");
            }else{

                variables = fillVariables(txtVariables.getText());
                algorithmParser = new AlgorithmParser(this,txtVariables.getText(),txtRulesAlgorithm.getText());
                algorithmParser.fillVarTable(variables);
                try {
                    rulesTable = algorithmParser.parse();
                    boolean error = algorithmParser.createActionListFile();
                    if(error){
                        JOptionPane.showMessageDialog(null,"Rules has NOT been created, fix all errors before continuing");
                    }else {
                        JOptionPane.showMessageDialog(null, "Rules has been created");
                        btnCompile.setEnabled(true);
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        });


        btnShowRoute.addActionListener(e -> {
            agentRoutePane.removeAll();
            mapsPane.setSelectedIndex(2);
            //nodeTxt = nodes.getText();
            Agent agent = agentNetwork.getAllAgents().get(Integer.valueOf(showRouteTxt.getText()));
            String[] log = agent.sendLog().split(",");
            Graph<String, String> g = new DelegateForest<>();
            if (agent != null) {
                if(!timedSecCheckBox.isSelected()) {
                    mapsPane.setSelectedIndex(2);
                    String lastChar = log[0];
                    g.addVertex(lastChar);
                    for (int i = 1; i < log.length; i++) {
                        String logged = log[i];
                        if (lastChar != logged) {
                            if (!g.containsVertex(logged)) {
                                g.addVertex(logged);

                            }

                        }
                        lastChar = logged;
                    }
                    for (int i = 1; i < log.length; i++) {
                        if (Integer.valueOf(log[i - 1]) < Integer.valueOf(log[i]))
                            g.addEdge(log[i - 1] + "-" + log[i], log[i - 1], log[i]);
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
                        if (i == "") return Color.GREEN;
                        return Color.RED;
                    };

                    Transformer<String, Shape> vertexSize1 = i -> {
                        Ellipse2D circle = new Ellipse2D.Double(-4, -4, 8, 8);
                        return circle;
                    };

                    vv3.getRenderContext().setVertexLabelTransformer(transformer);
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
                    new TimedTraversalSingle(agent,agentRoutePane,mapsPane,time).start();
                }

            }

        });

        resetButton.addActionListener(e -> {
            maps.removeAll();
            agentsMap.removeAll();
            agentRoutePane.removeAll();
            mapsPane.updateUI();
            drawBtn.setEnabled(true);
        });

        btnDrawAgents.addActionListener(e -> {
            if (trad != null) {
                Graph<String, String> g = new DelegateForest<>();
                mapsPane.setSelectedIndex(1);
                agentNetwork = new AgentNetwork(trad, Integer.parseInt(txtAgents.getText()));

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
                Transformer<String, Paint> vertexColor1 = i -> {
                    if (i.equals("0")) return Color.GREEN;
                    return Color.RED;
                };
                Transformer<String, Paint> edgesColor1 = i -> {
                    if (i == "") return Color.GREEN;
                    return Color.RED;
                };

                Transformer<String, Shape> vertexSize1 = i -> {
                    Ellipse2D circle = new Ellipse2D.Double(-4, -4, 8, 8);
                    return circle;
                };

                vv3.getRenderContext().setVertexLabelTransformer(transformer);
                vv3.getRenderContext().setVertexShapeTransformer(vertexSize1);
                //vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
                vv3.getRenderContext().setVertexFillPaintTransformer(vertexColor1);
                vv3.getRenderContext().setEdgeDrawPaintTransformer(edgesColor1);
                agentsMap.removeAll();
                agentsMap.add(vv3);
                agentsMap.requestFocus();
                agentsMap.updateUI();
                //  drawBtn.setEnabled(false);
            }
        });

        btnTraverse.addActionListener(e -> {
            if (!timedSecCheckBox.isSelected()) {
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
                    if (i.equals("0") || i.equals(txtRoot.getText())) return Color.GREEN;
                        //else if (!tempTree.containsValue(agentNetwork.getCurrentTree().get(i))) return Color.YELLOW;
                    else if (agentNetwork.getAllAgentLocations().contains(Integer.parseInt(i))) return Color.CYAN;
                    return Color.RED;
                };

                Transformer<String, Paint> edgesColorEdges = i -> {
                    if (i.equals("")) return Color.YELLOW;
                    return Color.RED;
                };

                Transformer<String, Shape> vertexSize1 = i -> {
                    Ellipse2D circle = new Ellipse2D.Double(-4, -4, 8, 8);
                    return circle;
                };

                vv3.getRenderContext().setVertexLabelTransformer(transformer);
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
                if(agentNetwork.checkIfAllAgentsInRoot() && agentNetwork.checkIfComplete()){
                    int[] fast =  agentNetwork.lookForFastestAgent();
                    int[] slow = agentNetwork.lookForSlowestAgent();
                    fastestAgent.setText("Fastest Agent: " + fast[0] + " time: " + fast[1]);
                    slowestAgent.setText("Slowest Agent" + slow[0] + " time: " + slow[1]);
                }
            } else {
                int time = Integer.parseInt(txtTime.getText());
                new TimedTraversal(trad, agentNetwork, mapsPane, agentsMap,time,fastestAgent,slowestAgent, agentTimeStepCounter,agentTimeStepCounterNumber).start();
            }
        });

        btnCompile.addActionListener(e -> {
            algorithmParser.compileActionList();
            JOptionPane.showMessageDialog(null,"Compiling complete!");
            btnInstance.setEnabled(true);
        });

        btnInstance.addActionListener(e -> {
            try {
                algorithmParser.instanciateActionList();
                JOptionPane.showMessageDialog(null,"Tree is ready to be drawn!");
                btnDrawNodes.setEnabled(true);
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null,"No Class found!");
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null,"URL is not correct!");
            }
        });

        btnDrawNodes.addActionListener(e -> {
            mapsPane.setSelectedIndex(0);
            DirectedGraph<String, String> g = new DelegateForest<>();
            trad = new Trad();

            if(!checkBoxSimpleDraw.isSelected()) {
                Rule root = rulesTable.get(txtRoot.getText());
                algorithmParser.executeRule(root, null, null);
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
            }else {
                trad.buildTree(Integer.parseInt(String.valueOf(txtSimpleDraw.getText())));

                for (Map.Entry<Integer, Node> nodes : trad.getAllNodes().entrySet()) {
                    g.addVertex("" + nodes.getValue().getId());
                }
                for (Edge edges : trad.getAllEdges()) {
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
            Transformer<String, Paint> vertexColor1 = i -> {
                if (i.equals("0") || i.equals(txtRoot.getText())) return Color.GREEN;
                return Color.RED;
            };
            Transformer<String, Paint> edgesColor1 = i -> {
                if (i.equals("")) return Color.GREEN;
                return Color.RED;
            };

            Transformer<String, Shape> vertexSize1 = i -> {
                Ellipse2D circle = new Ellipse2D.Double(-4, -4, 8, 8);
                return circle;
            };

            vv3.getRenderContext().setVertexLabelTransformer(transformer);
            vv3.getRenderContext().setVertexShapeTransformer(vertexSize1);
            //vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
            vv3.getRenderContext().setVertexFillPaintTransformer(vertexColor1);
            vv3.getRenderContext().setEdgeDrawPaintTransformer(edgesColor1);
            maps.removeAll();
            maps.add(vv3);
            maps.requestFocus();
            maps.updateUI();

        });
        checkBoxSimpleDraw.addActionListener(e -> {
            if (checkBoxSimpleDraw.isSelected()) {
                btnDrawNodes.setEnabled(true);
                btnDrawNodes.setText("Simple Node building");
            } else {
                if (btnInstance.isEnabled()) {
                    btnDrawNodes.setText("Draw Nodes");
                } else {
                    btnDrawNodes.setEnabled(false);
                    btnDrawNodes.setText("Draw Nodes");
                }
            }
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
}


