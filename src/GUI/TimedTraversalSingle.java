package GUI;

import AgentNetwork.AgentNetwork;
import Tree.Edge;
import Tree.Node;
import Tree.Trad;
import AgentNetwork.Agent;
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

public class TimedTraversalSingle extends Thread {

    private Agent agent;
    private VisualizationViewer vv3;
    private JTabbedPane mapsPane;
    private int time;
    private JPanel agentRoutePane;
    private Layout<Integer, String> layout3;

    public TimedTraversalSingle(Agent agent, JPanel agentRoutePane, JTabbedPane mapsPane, int time) {

        this.agent = agent;
        this.agentRoutePane = agentRoutePane;
        this.mapsPane = mapsPane;

        this.time = time;
    }
    @Override
    public void run() {
        Graph<Integer, String> g = new DelegateForest<>();
        mapsPane.setSelectedIndex(2);
        String[] log = agent.sendLog().split(",");
        int step = 0;
        Integer lastChar = Integer.valueOf(log[step]);
        g.addVertex(lastChar);



        do {
            step++;
            Integer logged = Integer.valueOf(log[step]);
            if (lastChar != logged) {
                if (!g.containsVertex(logged)) {
                    g.addVertex(logged);

                }

            }
            lastChar = logged;
            if (Integer.valueOf(log[step - 1]) < Integer.valueOf(log[step])
                    && !g.containsEdge(log[step - 1] + "-" + log[step])
                    && Integer.parseInt(log[step]) > Integer.parseInt(log[step-1])
                    && Integer.parseInt(log[step])!= 0) {
                g.addEdge(log[step - 1] + "-" + log[step], Integer.valueOf(log[step - 1]), Integer.valueOf(log[step]));

            }

            if(log.length<100) {
                layout3 = new TreeLayout<>((Forest<Integer, String>) g);
            }
            else {
                layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
            }
            vv3 = new VisualizationViewer<>(layout3);
            final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
            vv3.setGraphMouse(graphMouse3);
            graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

            Transformer<Integer, String> transformer = integer -> String.valueOf(integer);
            // Transformer maps the vertex number to a vertex property
            int finalStep = step;
            Transformer<Integer, Paint> vertexColor1 = i -> {
                if(i == Integer.parseInt(log[finalStep])) return Color.CYAN;
                else if (i == 0) return Color.GREEN;
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
            agentRoutePane.removeAll();
            agentRoutePane.add(vv3);
            agentRoutePane.requestFocus();
            agentRoutePane.updateUI();

            try {
                sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } while (step < log.length-1);
    }
}
