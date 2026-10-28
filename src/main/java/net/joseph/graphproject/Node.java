package net.joseph.graphproject;

import java.awt.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Node {

	private final int id;
	private final int x, y;
	private static final int RADIUS = 20;
	private Color color;
	private final int value;
	private final java.util.List<Connection> connections;

	public Node(int id, int x, int y, int value) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.color = Color.BLUE;
		this.value = value;
		connections = new ArrayList<>();
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public java.util.List<Connection> getConnections() {
		return Collections.unmodifiableList(connections);
	}

	public boolean contains(int px, int py) {
		int dx = px - x;
		int dy = py - y;
		return dx * dx + dy * dy <= RADIUS * RADIUS;
	}

	public void addConnection(Connection c) {
		for(Connection existing : connections) {
			if(existing.to().equals(c.to())) {
				return; // Already connected
			}
		}
		connections.add(c);
	}

	public boolean isConnectedTo(Node other) {
		for(Connection c : connections) {
			if(c.to().equals(other)) {
				return true;
			}
		}
		return false;
	}

	public void draw(Graphics2D g) {
		g.setColor(color);
		g.fillOval(x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS);
		g.setColor(Color.BLACK);
		g.drawOval(x - RADIUS, y - RADIUS, 2 * RADIUS, 2 * RADIUS);
		g.drawString(String.valueOf(id), x - 5, y + 5);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Node node)) return false;
		return id == node.id && x == node.x && y == node.y && value == node.value && Objects.equals(color, node.color) && Objects.equals(connections, node.connections);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value);
	}
}
