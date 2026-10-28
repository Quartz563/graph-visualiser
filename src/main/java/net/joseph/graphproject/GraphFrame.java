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
			case "Dijkstra's Algorithm" -> Pathfinder.runDijkstra(graphPanel.getGraph().getNodes(), start, end);
			case "A* Search" -> Pathfinder.runAStar(graphPanel.getGraph().getNodes(), start, end);
			default -> new ArrayList<>();
		};
	}


}
