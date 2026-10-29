package net.joseph.graphproject;

import java.util.*;

public class Graph {

	private final List<Node> nodes;
	private final List<Edge> edges;
	private final int numNodes;
	private final int width;
	private final int height;
	private final Random rand;

	public Graph(List<Node> nodes, List<Edge> edges, int height, int width) {
		this.nodes = nodes;
		this.edges = edges;
		numNodes = nodes.size();
		this.height = height;
		this.width = width;
		rand = null;
	}

	public Graph(int numNodes, int width, int height) {
		this(numNodes, width, height, new Random());
	}

	public Graph(int numNodes, int width, int height, Random random) {
		rand = random;
		this.numNodes = numNodes;
		this.width = width;
		this.height = height;
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
		generateNodes();
		generateRandomGraph();
	}

	private void generateNodes() {
		int gWidth = width - 100;
		int gHeight = height - 100;
		for (int i = 0; i < numNodes; i++) {
			int x  = rand.nextInt(gWidth) + 50;
			int y  = rand.nextInt(gHeight) + 50;
			nodes.add(new Node(i, x, y, rand.nextInt(100) + 10));
		}
	}

	private void generateRandomGraph() {
		ArrayList<Node> OutGraph = new ArrayList<>(nodes);
		ArrayList<Node> InGraph = new ArrayList<>();
		for(Node n : OutGraph) {
			if (InGraph.isEmpty()) {
				InGraph.add(n);
				continue;
			}
			int rNode = rand.nextInt(InGraph.size());
			Node selectedNode = InGraph.get(rNode);
			n.addConnection(new Connection(n, selectedNode, randomWeight()));
			selectedNode.addConnection(new Connection(selectedNode, n, randomWeight()));

			for (Node g : InGraph) {
				if (g == selectedNode) continue;
				int seed = (g.hashCode() * 31) ^ n.hashCode();
				Random pairRng = new Random(seed);
				int totalConnections = n.getConnections().size() + g.getConnections().size();
				int threshold = Math.max(10, 90 - (totalConnections * 10));
				if (pairRng.nextInt(100) < threshold) {
					n.addConnection(new Connection(n, g, randomWeight()));
					g.addConnection(new Connection(g, n, randomWeight()));
				}
			}
			InGraph.add(n);
		}
		for (Node n : InGraph) {
			if(n.getConnections().size() >= 2) continue;
			while (n.getConnections().size() < 2) {
				int val = rand.nextInt(InGraph.size());
				Node selectedNode = InGraph.get(val);
				if(!n.isConnectedTo(selectedNode) && !selectedNode.equals(n)) {
					n.addConnection(new Connection(n, selectedNode));
					selectedNode.addConnection(new Connection(selectedNode, n));
				}
			}
		}
		this.nodes.clear();
		this.edges.clear();
		this.nodes.addAll(InGraph);
		Set<String> addedEdges = new HashSet<>();
		for (Node node : InGraph) {
			for (Connection conn : node.getConnections()) {
				int id1 = conn.from().getId();
				int id2 = conn.to().getId();
				String edgeKey = Math.min(id1, id2) + "-" + Math.max(id1, id2);
				if (!addedEdges.contains(edgeKey)) {
					System.out.println("Creating edge: " + id1 + " -> " + id2);
					this.edges.add(new Edge(conn.from(), conn.to(), conn.value()));
					addedEdges.add(edgeKey);
				}
			}
		}
	}

	private int randomWeight() {
		return rand.nextInt(100);
	}

	public int getNodeSize() {
		return nodes.size();
	}

	public int getNodeId(int node) {
		return nodes.get(node).getId();
	}

	public int getNodeConnectionCount(int node) {
		return nodes.get(node).getConnections().size();
	}

	public List<Node> getNodes() {
		return Collections.unmodifiableList(nodes);
	}

	public List<Edge> getEdges() {
		return Collections.unmodifiableList(edges);
	}

	public void setNodeConnection(Node a, Node b, int weight) {
		a.addConnection(new Connection(a, b, weight));
		b.addConnection(new Connection(b, a, weight));
	}

	public void clearGraph() {
		this.nodes.clear();
		this.edges.clear();
	}

}
