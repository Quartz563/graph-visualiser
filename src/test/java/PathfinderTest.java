import net.joseph.graphproject.Connection;
import net.joseph.graphproject.Graph;
import net.joseph.graphproject.Node;
import net.joseph.graphproject.Pathfinder;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PathfinderTest {

    @FunctionalInterface
    interface Algorithm {
        List<Integer> find(List<Node> nodes, int start, int end);
    }

    static Stream<Arguments> algorithms() {
        return Stream.of(
                Arguments.of(Named.of("Dijkstra", (Algorithm) Pathfinder::runDijkstra)),
                Arguments.of(Named.of("A*", (Algorithm) Pathfinder::runAStar)));
    }

    private Graph generateTestGraph() {
        Graph graph;
        Node a = new Node(0, 0, 0, 1);
        Node b = new Node(1, 1, 0, 2);
        Node c = new Node(2, 2, 0, 3);
        Node d = new Node(3, 0, 0, 4);
        Node e = new Node(4, 0, 0, 5);
        Node f = new Node(5, 0, 0, 6);
        Node g = new Node(6, 0, 0, 7);
        graph = new Graph(List.of(a, b, c, d, e, f, g), List.of(), 0, 0);
        graph.setNodeConnection(a, b, 1);
        graph.setNodeConnection(b, c, 2);
        graph.setNodeConnection(c, d, 3);
        graph.setNodeConnection(d, e, 4);
        graph.setNodeConnection(e, a, 5);
        graph.setNodeConnection(a, f, 6);
        graph.setNodeConnection(b, f, 7);
        graph.setNodeConnection(c, f, 8);
        graph.setNodeConnection(d, f, 9);
        graph.setNodeConnection(e, f, 10);
        graph.setNodeConnection(b, e, 11);
        graph.setNodeConnection(a, c, 12);
        graph.setNodeConnection(a, d, 13);
        graph.setNodeConnection(b, d, 14);
        return graph;
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void startEqualsEndReturnsSingleNode(Algorithm algorithm) {
        Graph graph = generateTestGraph();
        List<Integer> path = algorithm.find(graph.getNodes(), graph.getNodeId(0), graph.getNodeId(0));
        assertEquals(List.of(0), path);
        assertEquals(0, Pathfinder.pathCost(graph.getNodes(), path));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void cheaperIndirectRouteOverDirect(Algorithm algorithm) {
        Graph graph = generateTestGraph();
        List<Integer> path = algorithm.find(graph.getNodes(), graph.getNodeId(1), graph.getNodeId(5));
        assertEquals(List.of(1, 5), path);
        assertEquals(7, Pathfinder.pathCost(graph.getNodes(), path));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void multiHopCheaper(Algorithm algorithm) {
        Graph graph = generateTestGraph();
        List<Integer> path = algorithm.find(graph.getNodes(), graph.getNodeId(0), graph.getNodeId(3));
        assertEquals(List.of(0, 1, 2, 3), path);
        assertEquals(6, Pathfinder.pathCost(graph.getNodes(), path));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void startEndEqual(Algorithm algorithm) {
        Graph graph = generateTestGraph();
        List<Integer> path = algorithm.find(graph.getNodes(), graph.getNodeId(0), graph.getNodeId(0));
        assertEquals(List.of(0), path);
        assertEquals(0, Pathfinder.pathCost(graph.getNodes(), path));
    }

    @ParameterizedTest
    @MethodSource("algorithms")
    void invalidNode(Algorithm algorithm) {
        Graph graph = generateTestGraph();
        List<Integer> path = algorithm.find(graph.getNodes(), graph.getNodeId(0), graph.getNodeId(6));
        assertEquals(List.of(), path);
    }

}
