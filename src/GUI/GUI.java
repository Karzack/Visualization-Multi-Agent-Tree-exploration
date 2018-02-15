package GUI;

import AgentNetwork.AgentNetwork;
import Tree.Edge;
import Tree.Node;
import Tree.Trad;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
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
import java.util.Map;

public class GUI extends JPanel {
    private JPanel mainPanel;
    private JTextField nodes;
    private JButton drawBtn;
    private JTextField agents;
    private JButton agentsBtn;
    private JTextPane textPane1;
    private JPanel maps;
    private JTextPane textPane2;
    private Trad trad;
    private String nodeTxt;


    public GUI() {
        Graph<Integer, String> g = new DelegateForest<>();
        trad = new Trad();
        trad.buildTree(8);
        AgentNetwork network = new AgentNetwork(trad, 10);

        while (trad.getTreeSize() != network.getTreeSize()){
            network.traverseExecute();

        }
        System.out.println("Tree with " + trad.getTreeSize() + " nodes explored in " + network.getTimestep() + " steps");
        network.getAllAgents().forEach(agent -> {
            System.out.println("Agent " + agent.getId() + " route: " + agent.sendLog());
        });

        for (Map.Entry<Integer, Node> nodes: trad.getAllNodes().entrySet())
        {

            g.addVertex(nodes.getValue().getId());

        }
        for (Map.Entry<String, Edge> edges: trad.getAllEdges().entrySet())
        {
            g.addEdge(edges.getKey(),edges.getValue().getParent().getId(),edges.getValue().getChild().getId());
        }



        Layout<Integer, String> layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
        VisualizationViewer<Integer, String> vv3; vv3 = new VisualizationViewer<>(layout3);

        final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
        vv3.setGraphMouse(graphMouse3);
        graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

        Transformer<Integer, String> transformer = new Transformer<Integer, String>() {

            @Override
            public String transform(Integer integer) {
                return String.valueOf(integer);
            }

        };
        // Transformer maps the vertex number to a vertex property
        Transformer<Integer,Paint> vertexColor = new Transformer<Integer,Paint>() {
            public Paint transform(Integer i) {
                if(i == 0) return Color.GREEN;
                return Color.RED;
            }
        };

        Transformer<Integer,Shape> vertexSize = new Transformer<Integer,Shape>(){
            public Shape transform(Integer i){
                Ellipse2D circle = new Ellipse2D.Double(0, 0, 8, 8);
                return circle;
            }
        };

        vv3.getRenderContext().setVertexLabelTransformer(transformer);
        vv3.getRenderContext().setVertexShapeTransformer(vertexSize);
        //vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv3.getRenderContext().setVertexFillPaintTransformer(vertexColor);
        maps.add(vv3);

        drawBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                nodeTxt =nodes.getText();
                System.out.println(Integer.parseInt(nodeTxt));


            }
        });

    }

    public JPanel getPanel(){
        return mainPanel;
    }
}
