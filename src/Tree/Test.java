package Tree;

import java.util.Map;

import javax.swing.JFrame;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import org.apache.commons.collections15.Transformer;


public class Test {
    public static void main(String[] args) {
        {


     Trad trad = new Trad();
        trad.buildTree(500);
       JFrame frame3 = new JFrame("Multi-agents");

        Graph<Integer, String> g = new DelegateForest<>();
        for (Map.Entry<Integer,Node> nodes: trad.getAllNodes().entrySet())
        {

            g.addVertex(nodes.getValue().getId());

        }
        for (Map.Entry<String,Edge> edges: trad.getAllEdges().entrySet())
        {
            g.addEdge(edges.getKey(),edges.getValue().getParent().getId(),edges.getValue().getChild().getId());
        }



        Layout<Integer, String> layout3 = new RadialTreeLayout<>((Forest<Integer, String>) g);
        VisualizationViewer<Integer, String> vv3 = new VisualizationViewer<>(layout3);

        final DefaultModalGraphMouse<String, Number> graphMouse3 = new DefaultModalGraphMouse<>();
        vv3.setGraphMouse(graphMouse3);
        graphMouse3.setMode(ModalGraphMouse.Mode.PICKING);

        Transformer<Integer, String> transformer = new Transformer<Integer, String>() {

            @Override
            public String transform(Integer integer) {
                return String.valueOf(integer);
            }
        };
        vv3.getRenderContext().setVertexLabelTransformer(transformer);

        vv3.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        frame3.getContentPane().add(vv3);
        frame3.setSize(1500, 1500);
        frame3.pack();
        frame3.setVisible(true);
    }

}
}



