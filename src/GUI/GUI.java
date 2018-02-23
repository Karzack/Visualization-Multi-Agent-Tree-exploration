package GUI;

import AgentNetwork.AgentNetwork;
import AgentNetwork.Agent;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JPanel {
    private JPanel mainPanel;
    private JTextField nodes;
    private JButton drawBtn;
    private JTextField agents;
    private JButton agentsBtn;
    private JButton traverseBtn;
    private JTabbedPane mapsPane;
    private JPanel maps;
    private JPanel agentsMap;
    private JTextField showRouteTxt;
    private JButton showRouteBtn;
    private JLabel routeName;
    private JPanel agentRoutePane;
    private JCheckBox timedTraversalCheckBox;
    private JButton resetBtn;
    private JButton fileReaderBtn;
    private Trad trad;
    private AgentNetwork agentNetwork;
    private String nodeTxt;
    private VisualizationViewer<Integer, String> vv3;
    private JFileChooser fc = new JFileChooser();


    public GUI() {

        agentsBtn.setEnabled(false);
        // Transformer maps the vertex number to a vertex property
        Transformer<Integer, Paint> vertexColor = new Transformer<Integer, Paint>() {
            public Paint transform(Integer i) {
                if (i == 0) return Color.GREEN;
                return Color.RED;
            }
        };
        Transformer<String, Paint> edgesColor = new Transformer<String, Paint>() {
            public Paint transform(String i) {
                if (i == "") return Color.GREEN;
                return Color.RED;
            }
        };

        Transformer<Integer, Shape> vertexSize = new Transformer<Integer, Shape>() {
            public Shape transform(Integer i) {
                Ellipse2D circle = new Ellipse2D.Double(0, 0, 8, 8);
                return circle;
            }
        };

        drawBtn.addActionListener(e -> {
            mapsPane.setSelectedIndex(0);
            nodeTxt = nodes.getText();
            if (nodeTxt != null) {
                DirectedGraph<Integer, String> g = new DelegateForest<>();
                trad = new Trad();
                trad.buildTree(Integer.parseInt(nodeTxt));

                for (Map.Entry<Integer, Node> nodes : trad.getAllNodes().entrySet()) {

                    g.addVertex(nodes.getValue().getId());

                }
                for (Map.Entry<String, Edge> edges : trad.getAllEdges().entrySet()) {
                    g.addEdge(edges.getKey(), edges.getValue().getParent().getId(), edges.getValue().getChild().getId());
                }
                //g.addEdge("1-0",1,0);


                Layout<Integer, String> layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
                vv3 = new VisualizationViewer<>(layout3);

                final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
                vv3.setGraphMouse(graphMouse3);
                graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

                Transformer<Integer, String> transformer = integer -> String.valueOf(integer);
                // Transformer maps the vertex number to a vertex property
                Transformer<Integer, Paint> vertexColor1 = i -> {
                    if (i == 0) return Color.GREEN;
                    return Color.RED;
                };
                Transformer<String, Paint> edgesColor1 = i -> {
                    if (i == "") return Color.GREEN;
                    return Color.RED;
                };

                Transformer<Integer, Shape> vertexSize1 = i -> {
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
                agentsBtn.setEnabled(true);

            }


        });

        agentsBtn.addActionListener(e -> {
            if (trad != null) {
                Graph<Integer, String> g = new DelegateForest<>();
                mapsPane.setSelectedIndex(1);
                agentNetwork = new AgentNetwork(trad, Integer.parseInt(agents.getText()));

                for (Map.Entry<Integer, Node> nodes : agentNetwork.getCurrentTree().entrySet()) {

                    g.addVertex(nodes.getValue().getId());

                }

                for (Map.Entry<String, Edge> edges : trad.getAllEdges().entrySet()) {
                    if (agentNetwork.getCurrentTree().containsValue(edges.getValue().getChild())) {
                        g.addEdge(edges.getKey(), edges.getValue().getParent().getId(), edges.getValue().getChild().getId());
                    }
                }

                Layout<Integer, String> layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
                vv3 = new VisualizationViewer<>(layout3);

                final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
                vv3.setGraphMouse(graphMouse3);
                graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

                Transformer<Integer, String> transformer = integer -> String.valueOf(integer);
                // Transformer maps the vertex number to a vertex property
                Transformer<Integer, Paint> vertexColor1 = i -> {
                    if (i == 0) return Color.GREEN;
                    return Color.RED;
                };
                Transformer<String, Paint> edgesColor1 = i -> {
                    if (i == "") return Color.GREEN;
                    return Color.RED;
                };

                Transformer<Integer, Shape> vertexSize1 = i -> {
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
                drawBtn.setEnabled(false);
            }
        });

        traverseBtn.addActionListener(e -> {
            if (!timedTraversalCheckBox.isSelected()) {
                HashMap<Integer, Node> tempTree = (HashMap<Integer, Node>) agentNetwork.getCurrentTree().clone();
                agentNetwork.traverseExecute();
                Graph<Integer, String> g = new DelegateForest<>();
                mapsPane.setSelectedIndex(1);

                for (Map.Entry<Integer, Node> nodes : agentNetwork.getCurrentTree().entrySet()) {

                    g.addVertex(nodes.getValue().getId());

                }

                for (Map.Entry<String, Edge> edges : trad.getAllEdges().entrySet()) {
                    if (agentNetwork.getCurrentTree().containsValue(edges.getValue().getChild())) {
                        g.addEdge(edges.getKey(), edges.getValue().getParent().getId(), edges.getValue().getChild().getId());
                    }
                }

                Layout<Integer, String> layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
                vv3 = new VisualizationViewer<>(layout3);

                final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
                vv3.setGraphMouse(graphMouse3);
                graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

                Transformer<Integer, String> transformer = integer -> String.valueOf(integer);
                // Transformer maps the vertex number to a vertex property
                Transformer<Integer, Paint> vertexColorNodes = i -> {
                    if (i == 0) return Color.GREEN;
                    else if (!tempTree.containsValue(agentNetwork.getCurrentTree().get(i))) return Color.YELLOW;
                    else if (agentNetwork.getAllAgentLocations().contains(i)) return Color.CYAN;
                    return Color.RED;
                };

                Transformer<String, Paint> edgesColorEdges = i -> {
                    if (i == "") return Color.YELLOW;
                    return Color.RED;
                };

                Transformer<Integer, Shape> vertexSize1 = i -> {
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
            } else {
                new TimedTraversal(trad, agentNetwork, mapsPane, agentsMap).start();
            }
        });


        showRouteBtn.addActionListener(e -> {
            agentRoutePane.removeAll();
            mapsPane.setSelectedIndex(2);
            nodeTxt = nodes.getText();
            Agent agent = agentNetwork.getAllAgents().get(Integer.valueOf(showRouteTxt.getText()));
            String[] log = agent.sendLog().split(",");
            Graph<Integer, String> g = new DelegateForest<>();
            if (agent != null) {
                mapsPane.setSelectedIndex(2);
                routeName.setText(agent.sendLog());
                Integer lastChar = Integer.valueOf(log[0]);
                g.addVertex(lastChar);
                for (int i = 1; i < log.length; i++) {
                    Integer logged = Integer.valueOf(log[i]);
                    if (lastChar != logged) {
                        if (!g.containsVertex(logged)) {
                            g.addVertex(logged);

                        }

                    }
                    lastChar = logged;
                }
                for (int i = 1; i < log.length; i++) {
                    if (Integer.valueOf(log[i - 1]) < Integer.valueOf(log[i]))
                        g.addEdge(log[i - 1] + "-" + log[i], Integer.valueOf(log[i - 1]), Integer.valueOf(log[i]));
                }


                Layout<Integer, String> layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
                vv3 = new VisualizationViewer<>(layout3);

                final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
                vv3.setGraphMouse(graphMouse3);
                graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

                Transformer<Integer, String> transformer = integer -> String.valueOf(integer);
                // Transformer maps the vertex number to a vertex property
                Transformer<Integer, Paint> vertexColor1 = i -> {
                    if (i == 0) return Color.GREEN;
                    return Color.RED;
                };
                Transformer<String, Paint> edgesColor1 = i -> {
                    if (i == "") return Color.GREEN;
                    return Color.RED;
                };

                Transformer<Integer, Shape> vertexSize1 = i -> {
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

        });

        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                maps.removeAll();
                agentsMap.removeAll();
                agentRoutePane.removeAll();
                mapsPane.updateUI();
                drawBtn.setEnabled(true);
            }
        });
        fileReaderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File file = null;
                int returnVal = fc.showOpenDialog(GUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        file = fc.getSelectedFile();
                    } catch (Exception e1) {

                    }

                    try {
                        FileReader fr = new FileReader(file);
                        BufferedReader br = new BufferedReader(fr);

                        String str;
                        while ((str = br.readLine()) != null) {
                            System.out.println(str);
                            JOptionPane.showMessageDialog(null, str);

                        }
                        br.close();
                    } catch (FileNotFoundException e1) {
                        JOptionPane.showMessageDialog(null, "File not found");
                    } catch (IOException e1) {
                        JOptionPane.showMessageDialog(null, "IO exception");
                    }
                }
            }
        });
    }
    

    public JPanel getPanel(){
        return mainPanel;
    }



}


