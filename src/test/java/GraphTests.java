import net.joseph.graphproject.Graph;
import net.joseph.graphproject.Node;
import net.joseph.graphproject.Pathfinder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphTests {

    private static final int NODE_COUNT = 15;
    static LongStream seeds() {
        return LongStream.range(0, 200);
    }

    @ParameterizedTest(name = "seed {0}")
    @MethodSource("seeds")
    void aStarFindsSameCostAsDijkstra(long seed) {
        Graph graph = new Graph(NODE_COUNT, 800, 600, new Random(seed));
        List<Node> nodes = graph.getNodes();
        int end = nodes.size() - 1;

        int dijkstraCost = Pathfinder.pathCost(nodes, Pathfinder.runDijkstra(nodes, 0, end));
        int aStarCost = Pathfinder.pathCost(nodes, Pathfinder.runAStar(nodes, 0, end));

        assertEquals(dijkstraCost, aStarCost, "A* path cost differs from Dijkstra");
    }

}
