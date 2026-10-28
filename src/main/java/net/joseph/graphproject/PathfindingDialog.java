package net.joseph.graphproject;

import javax.swing.*;
import java.awt.*;

public class PathfindingDialog extends JDialog {
	private final JComboBox<String> algoCombo;
	private final JSpinner startNodeSpinner;
	private final JSpinner endNodeSpinner;
	private boolean confirmed = false;

	public PathfindingDialog(JFrame parent, int maxNodeId) {
		super(parent, "Pathfinding Configuration", true);
		setLayout(new BorderLayout(10, 10));
		setSize(400, 250);
		setLocationRelativeTo(parent);
		JPanel formPanel = new JPanel(new GridLayout(3, 2, 10,10));
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 10));
		formPanel.add(new JLabel("Start Node:"));
		startNodeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, maxNodeId, 1));
		formPanel.add(startNodeSpinner);
		formPanel.add(new JLabel("End Node:"));
		endNodeSpinner = new JSpinner(new SpinnerNumberModel(maxNodeId, 0, maxNodeId, 1));
		formPanel.add(endNodeSpinner);
		formPanel.add(new JLabel("Algorithm:"));
		String[] algorithms = {"Dijkstra's Algorithm", "A* Search"};
		algoCombo = new JComboBox<>(algorithms);
		formPanel.add(algoCombo);
		add(formPanel, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
		JButton runButton = new JButton("Run");
		runButton.addActionListener(e -> {
			if(validateInput()) {
				confirmed = true;
				dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			confirmed = false;
			dispose();
		});
		buttonPanel.add(runButton);
		buttonPanel.add(cancelButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	private boolean validateInput() {
		int start = getStartNode();
		int end = getEndNode();

		if (start == end) {
			JOptionPane.showMessageDialog(this,
					"Start and end nodes must be different!",
					"Invalid Input",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public int getStartNode() {
		return (Integer) startNodeSpinner.getValue();
	}

	public int getEndNode() {
		return (Integer) endNodeSpinner.getValue();
	}

	public String getSelectedAlgorithm() {
		return (String) algoCombo.getSelectedItem();
	}
}
