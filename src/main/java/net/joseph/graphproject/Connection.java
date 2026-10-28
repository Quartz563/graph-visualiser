package net.joseph.graphproject;

import java.util.Random;

public record Connection(Node from, Node to, int value) {
	private static final Random random = new Random(); // Reuse one instance

	public Connection(Node from, Node to) {
		this(from, to, random.nextInt(100) + 1);
	}


}
