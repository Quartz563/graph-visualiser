package net.joseph.graphproject;

import javax.swing.*;

public class GraphVisualizer {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> {
			GraphFrame frame = new GraphFrame();
			frame.setVisible(true);
		});
	}

}
