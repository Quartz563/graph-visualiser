package net.joseph.graphproject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPanel extends JPanel {
	private Graph graph;

	public GraphPanel(Graph graph) {
		this.graph = graph; //always default to 5
		setBackground(Color.WHITE);
	}

	public void generateRandomGraph(int nodes) {
		graph = new Graph(nodes, getWidth(), getHeight());
		System.out.println("Generated " + graph.getNodeSize() + " nodes");
		System.out.println("Total edges created: " + graph.getEdges().size());
		for (int i = 0; i < graph.getNodeSize(); i++) {
			System.out.println("Node " + graph.getNodeId(i) + " has " + graph.getNodeConnectionCount(i) + " connections");
		}

		repaint();
	}

	public void highlightPath(List<Integer> path) {
		for (Node n : graph.getNodes()) {
			n.setColor(Color.BLUE);
		}
		for (Edge e : graph.getEdges()) {
			e.setColor(Color.BLACK);
		}
		for (int id : path) {
			if (id < graph.getNodeSize()) {
				graph.getNodes().get(id).setColor(Color.GREEN);
			}
		}

		for (int i = 0; i < path.size() - 1; i++) {
			int fromId = path.get(i);
			int toId = path.get(i + 1);
			for (Edge e : graph.getEdges()) {
				if ((e.getFrom().getId() == fromId && e.getTo().getId() == toId) ||
						(e.getFrom().getId() == toId && e.getTo().getId() == fromId)) {
					e.setColor(Color.GREEN);
				}
			}
		}
		repaint();
	}

	public void clearGraph() {
		graph.clearGraph();
		repaint();
	}

	public Graph getGraph() {
		return graph;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw edges first
		for (Edge e : graph.getEdges()) {
			e.draw(g2);
		}

		// Draw nodes on top
		for (Node n : graph.getNodes()) {
			n.draw(g2);
		}
	}
}
