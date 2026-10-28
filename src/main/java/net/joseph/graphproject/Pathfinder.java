package net.joseph.graphproject;

import java.util.*;

/**
 * Shortest-path algorithms over a graph's nodes. Kept separate from the Swing
 * classes so the algorithms can be tested without a window.
 */
public final class Pathfinder {

    private Pathfinder(){}

    /**
     * Sums the connection weights along a path.
     *
     * @throws IllegalArgumentException if two consecutive nodes in the path are not connected
     */
    public static int pathCost(List<Node> nodes, List<Integer> path) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node from = nodes.get(path.get(i));
            int toId = path.get(i + 1);
            Connection step = null;
            for(Connection c: from.getConnections()) {
                if(c.to().getId() == toId) {
                    step = c;
                    break;
                }
            }
            if(step == null) {
                throw new IllegalArgumentException("Nodes " + from.getId() + " and" + toId + " are not connected.");
            }
            total += step.value();
        }
        return total;
    }

    /**
     * Finds the lowest-cost path between two nodes using Dijkstra's algorithm.
     *
     * @return node ids from start to end inclusive, or an empty list if no path exists
     */
    public static List<Integer> runDijkstra(List<Node> nodes, int start, int end) {
        int nodesExplored = 0;
        if(start >= nodes.size() || end >= nodes.size()) {
            return new ArrayList<>();
        }
        Map<Integer, Double> distances = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(
                Comparator.comparingDouble(nd -> nd.distance)
        );
        for (Node node : nodes) {
            distances.put(node.getId(), Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        pq.offer(new NodeDistance(start, 0.0));
        while(!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            int currentId = current.nodeId;
            if(visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);
            nodesExplored++;
            if(currentId == end) {
                break;
            }
            Node currentNode = nodes.get(currentId);
            for (Connection conn : currentNode.getConnections()) {
                int neighborId = conn.to().getId();

                // Skip if already visited
                if (visited.contains(neighborId)) {
                    continue;
                }

                // Calculate new distance through current node
                double newDistance = distances.get(currentId) + conn.value();

                // If this path is shorter, update it
                if (newDistance < distances.get(neighborId)) {
                    distances.put(neighborId, newDistance);
                    previous.put(neighborId, currentId);
                    pq.offer(new NodeDistance(neighborId, newDistance));
                }
            }
        }
        System.out.println("Nodes explored: " + nodesExplored + " / " + nodes.size());
        return reconstructPath(previous, start, end);
    }

    /**
     * Finds a path between two nodes using A* search, guided by the straight-line
     * distance between node positions.
     *
     * @return node ids from start to end inclusive, or an empty list if no path exists
     */
    public static List<Integer> runAStar(List<Node> nodes, int start, int end) {
        int nodesExplored = 0;
        if(start >= nodes.size() || end >= nodes.size()) {
            return new ArrayList<>();
        }
        Node endNode = nodes.get(end);
        Map<Integer, Double> gScore = new HashMap<>();
        Map<Integer, Double> fScore = new HashMap<>();
        Map<Integer, Integer> previous = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(n -> n.fScore)
        );
        for(Node n : nodes) {
            gScore.put(n.getId(), Double.POSITIVE_INFINITY);
            fScore.put(n.getId(), Double.POSITIVE_INFINITY);
        }
        gScore.put(start, 0.0);
        fScore.put(start, heuristic(nodes.get(start), endNode));
        openSet.offer(new AStarNode(start, fScore.get(start)));
        while(!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            int currentId = current.nodeId;
            if(visited.contains(currentId)) {
                continue;
            }
            visited.add(currentId);
            nodesExplored++;
            if(currentId == end) {
                break;
            }
            Node currentNode = nodes.get(currentId);
            for (Connection conn : currentNode.getConnections()) {
                int neighborId = conn.to().getId();
                if(visited.contains(neighborId)) {
                    continue;
                }
                double tentativeGScore = gScore.get(currentId) + conn.value();
                if(tentativeGScore < gScore.get(neighborId)) {
                    previous.put(neighborId, currentId);
                    gScore.put(neighborId, tentativeGScore);
                    double heuristicCost = heuristic(conn.to(), endNode);
                    fScore.put(neighborId, tentativeGScore + heuristicCost);
                    openSet.offer(new AStarNode(neighborId, fScore.get(neighborId)));
                }
            }
        }
        System.out.println("Nodes explored: " + nodesExplored + " / " + nodes.size());
        return reconstructPath(previous, start, end);
    }

    private static List<Integer> reconstructPath(Map<Integer, Integer> previous, int start, int end) {
        List<Integer> path = new ArrayList<>();

        // Check if path exists
        if (!previous.containsKey(end) && start != end) {
            return path; // No path found
        }

        // Trace back from end to start
        Integer current = end;
        while (current != null) {
            path.add(current);
            if (current == start) {
                break;
            }
            current = previous.get(current);
        }

        // Reverse to get start -> end order
        Collections.reverse(path);
        System.out.println("Steps taken: " + path.size());
        return path;
    }

    private static double heuristic(Node from, Node to) {
        int dx = from.getX() - to.getX();
        int dy = from.getY() - to.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static class NodeDistance {
        int nodeId;
        double distance;
        public NodeDistance(int nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }
    }

    private static class AStarNode {
        int nodeId;
        double fScore;

        public AStarNode(int nodeId, double fScore) {
            this.nodeId = nodeId;
            this.fScore = fScore;
        }
    }

}
