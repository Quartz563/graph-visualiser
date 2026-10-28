package net.joseph.graphproject;

import java.awt.*;

class Edge {
	private Node from;
	private Node to;
	private double weight;
	private Color color;

	public Edge(Node from, Node to, double weight) {
		this.from = from;
		this.to = to;
		this.weight = weight;
		this.color = Color.BLACK;
	}

	public Node getFrom() { return from; }
	public Node getTo() { return to; }
	public double getWeight() { return weight; }
	public Color getColor() { return color; }
	public void setColor(Color c) { this.color = c; }

	public void draw(Graphics2D g) {
		g.setColor(color);
		g.setStroke(new BasicStroke(2));
		g.drawLine(from.getX(), from.getY(), to.getX(), to.getY());

		int midX = (from.getX() + to.getX()) / 2;
		int midY = (from.getY() + to.getY()) / 2;
		g.setColor(Color.RED);
		g.drawString(String.format("%.0f", weight), midX, midY);
	}
}