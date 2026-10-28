package net.joseph.graphproject;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphFrame extends JFrame {
	private final GraphPanel graphPanel;
	private final JTextField nodeCountField;

	public GraphFrame() {
		setTitle("Graph Pathfinding Visualizer");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		graphPanel = new GraphPanel(new Graph(5, getWidth(), getHeight()));
		add(graphPanel, BorderLayout.CENTER);

		JPanel controlPanel = new JPanel();

		JLabel nodeLabel = new JLabel("Number of Nodes:");
		nodeCountField = new JTextField("10", 5);

		JButton generateBtn = new JButton("Generate Graph");
		generateBtn.addActionListener(e -> {
			try {
				int numNodes = Integer.parseInt(nodeCountField.getText());
				if (numNodes > 0 && numNodes <= 100) {
					graphPanel.generateRandomGraph(numNodes);
				} else {
					JOptionPane.showMessageDialog(this,
							"Please enter a number between 1 and 100");
				}
			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this,
						"Please enter a valid number");
			}
		});

		JButton clearBtn = new JButton("Clear Graph");
		clearBtn.addActionListener(e -> graphPanel.clearGraph());

		JButton runPathfindingBtn = getJButton();

		controlPanel.add(nodeLabel);
		controlPanel.add(nodeCountField);
		controlPanel.add(generateBtn);
		controlPanel.add(runPathfindingBtn);
		controlPanel.add(clearBtn);

		add(controlPanel, BorderLayout.SOUTH);
	}

	private JButton getJButton() {
		JButton runPathfindingBtn = new JButton("Run Pathfinding");
		runPathfindingBtn.addActionListener(e -> {
			List<Node> nodes = graphPanel.getGraph().getNodes();

			if (nodes.isEmpty()) {
				JOptionPane.showMessageDialog(this,
						"Please generate a graph first!",
						"No Graph",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Show the dialog
			PathfindingDialog dialog = new PathfindingDialog(this, nodes.size() - 1);
			dialog.setVisible(true);

			// If user clicked Run
			if (dialog.isConfirmed()) {
				int startNode = dialog.getStartNode();
				int endNode = dialog.getEndNode();
				String algorithm = dialog.getSelectedAlgorithm();

				System.out.println("Running " + algorithm);
				System.out.println("Start: " + startNode + ", End: " + endNode);

				// Call your pathfinding implementation
				List<Integer> path = runPathfinding(startNode, endNode, algorithm);

				if (path != null && !path.isEmpty()) {
					graphPanel.highlightPath(path);
				} else {
					JOptionPane.showMessageDialog(this,
							"No path found between nodes " + startNode + " and " + endNode,
							"No Path",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		return runPathfindingBtn;
	}

	private List<Integer> runPathfinding(int start, int end, String algorithm) {
		return switch (algorithm) {
			case "Dijkstra's Algorithm" -> runDijkstra(start, end);
			case "A* Search" -> runAStar(start, end);
			default -> new ArrayList<>();
		};
	}

	private List<Integer> runDijkstra(int start, int end) {
		int nodesExplored = 0;
		List<Node> nodes = graphPanel.getGraph().getNodes();
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
				int neighborId = conn.getTo().getId();

				// Skip if already visited
				if (visited.contains(neighborId)) {
					continue;
				}

				// Calculate new distance through current node
				double newDistance = distances.get(currentId) + conn.getValue();

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

	private List<Integer> runAStar(int start, int end) {
		int nodesExplored = 0;
		List<Node> nodes = graphPanel.getGraph().getNodes();
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
				int neighborId = conn.getTo().getId();
				if(visited.contains(neighborId)) {
					continue;
				}
				double tentativeGScore = gScore.get(currentId) + conn.getValue();
				if(tentativeGScore < gScore.get(neighborId)) {
					previous.put(neighborId, currentId);
					gScore.put(neighborId, tentativeGScore);
					double heuristicCost = heuristic(conn.getTo(), endNode);
					fScore.put(neighborId, tentativeGScore + heuristicCost);
					openSet.offer(new AStarNode(neighborId, fScore.get(neighborId)));
				}
			}
		}
		System.out.println("Nodes explored: " + nodesExplored + " / " + nodes.size());
		return reconstructPath(previous, start, end);
	}

	private List<Integer> reconstructPath(Map<Integer, Integer> previous, int start, int end) {
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

	private double heuristic(Node from, Node to) {
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
