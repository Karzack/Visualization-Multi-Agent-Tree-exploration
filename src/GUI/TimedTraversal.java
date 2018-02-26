package GUI;

import AgentNetwork.AgentNetwork;
import Tree.Edge;
import Tree.Node;
import Tree.Trad;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.RadialTreeLayout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Map;

public class TimedTraversal extends Thread {
    private AgentNetwork agentNetwork;
    private VisualizationViewer vv3;
    private Trad trad;
    private JTabbedPane mapsPane;
    private JPanel agentsMap;
    private int time;

    public TimedTraversal(Trad trad, AgentNetwork agentNetwork, JTabbedPane mapsPane, JPanel agentsMap, int time) {
        this.trad = trad;
        this.agentNetwork = agentNetwork;
        this.mapsPane = mapsPane;
        this.agentsMap = agentsMap;

        this.time = time;
    }

    @Override
    public void run() {
        do {
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
            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }while (!agentNetwork.checkIfComplete());
    }
}
