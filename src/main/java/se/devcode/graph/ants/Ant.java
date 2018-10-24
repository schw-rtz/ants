package se.devcode.graph.ants;

import java.util.Objects;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class Ant {

	private Vertex position;
	private int waittime = 0;
	private Mode mode = Mode.LFF;
	private int antName;

	public Ant(int n, Vertex a) {
		antName = n;
		position = a;
	}

	public int getWaittime() {
		return waittime;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public void decreaseWait() {
		waittime--;
	}

	public boolean isOnTransport() {
		return Objects.equals(position.property("typ").value(), "transport");
	}

	public Vertex getPosition() {
		return position;
	}

	public void setPosition(Vertex nextTransport) {
		this.position = nextTransport;
	}

	public void setWaittime(int waittime) {
		this.waittime = waittime;
	}

	public void makeItSmell() {
		if (mode == Mode.LFF) {
			Integer lff = (Integer) position.property("lff").value();
			position.property("lff", lff + 1);
		}
		if (mode == Mode.FF) {
			Integer ff = (Integer) position.property("ff").value();
			position.property("ff", ff + 1);
		}
	}

	public boolean isInAPlace() {
		return Objects.equals(position.property("typ").value(), "plats");
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public String toString() {
		return "Ant [antName=" + antName + ", mode=" + mode + ", position=" + position + ", waittime=" + waittime + "]\n";
	}

}
