package GUI;

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

class TimedTraversalSingle extends Thread {

    private Agent agent;
    private JTabbedPane mapsPane;
    private int time;
    private boolean showNodes;
    private int currentRouteStep;
    private JPanel agentRoutePane;
    private boolean continueWhile=true;

    public TimedTraversalSingle(Agent agent, int currentRouteStep, JPanel agentRoutePane, JTabbedPane mapsPane, int time, boolean showNodes) {

        this.agent = agent;
        this.currentRouteStep = currentRouteStep;
        this.agentRoutePane = agentRoutePane;
        this.mapsPane = mapsPane;

        this.time = time;
        this.showNodes = showNodes;
    }
    @Override
    public void run() {
        Graph<String, String> g = new DelegateForest<>();
        mapsPane.setSelectedIndex(2);
        String[] log = agent.sendLog().split(",");

        String lastChar = log[currentRouteStep];
        g.addVertex(log[currentRouteStep]);



        do {

            String logged = log[currentRouteStep];
            if (!lastChar.equals(logged)) {
                if (!g.containsVertex(logged)) {
                    g.addVertex(logged);

                }

            }
            lastChar = logged;
            if(currentRouteStep>0) {
                if (!g.containsEdge(log[currentRouteStep - 1] + "-" + log[currentRouteStep])
                        && !g.containsEdge(log[currentRouteStep] + "-" + log[currentRouteStep - 1])) {
                    g.addEdge(log[currentRouteStep - 1] + "-" + log[currentRouteStep], log[currentRouteStep - 1], log[currentRouteStep]);
                }
            }
            Layout<String,String> layout3;
            if(log.length<100) {
                layout3 = new TreeLayout<>((Forest<String, String>) g);
            }
            else {
                layout3 = new RadialTreeLayout<>((Forest<String, String>) g);
            }
            VisualizationViewer vv3 = new VisualizationViewer<>(layout3);
            final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
            vv3.setGraphMouse(graphMouse3);
            graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

            Transformer<String, String> transformer = String::valueOf;
            // Transformer maps the vertex number to a vertex property
            int finalStep = currentRouteStep;
            Transformer<String, Paint> vertexColor1 = i -> {
                if(i.equals(log[finalStep])) return Color.CYAN;
                else if (i.equals(agent.getNetwork().getRoot().getName())) return Color.GREEN;
                return Color.RED;
            };
            Transformer<String, Paint> edgesColor1 = i -> {
                if (i.equals("")) return Color.GREEN;
                return Color.RED;
            };

            Transformer<String, Shape> vertexSize1 = i -> (Ellipse2D) new Ellipse2D.Double(-4, -4, 8, 8);

            if(showNodes) {
                vv3.getRenderContext().setVertexLabelTransformer(transformer);
            }
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
            currentRouteStep++;
        } while (currentRouteStep < log.length-1 && continueWhile);
    }

    public void stopWriting() {
        continueWhile=false;
    }
}
