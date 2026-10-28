package net.joseph.graphproject;

import java.security.SecureRandom;
import java.util.Random;

public class Connection {
	private final Node from;
	private final Node to;
	private final int value;
	private static final Random random = new Random(); // Reuse one instance

	public Connection(Node from, Node to) {
		this.from = from;
		this.to = to;
		this.value = random.nextInt(100) + 1; // 1-100 range
	}

	public Node getFrom() { return from; }
	public Node getTo() { return to; }
	public int getValue() { return value; }
}
