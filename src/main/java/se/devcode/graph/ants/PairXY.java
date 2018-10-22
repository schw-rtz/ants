package se.devcode.graph.ants;

import java.util.Comparator;

public class PairXY<X, Y> {
	private X x;
	private Y y;

	public PairXY(X x, Y y) {
		super();
		this.x = x;
		this.y = y;
	}

	public X getX() {
		return x;
	}

	public Y getY() {
		return y;
	}

}
