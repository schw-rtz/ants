package se.devcode.graph.ants;

import java.util.Objects;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import com.thinkaurelius.titan.core.TitanVertex;

public class Ant {

	private Vertex position;
	private int waittime = 0;
	private Mode mode = Mode.LFF;

	public Ant(TitanVertex a) {
		position = a;
	}

	public int getWaittime() {
		return waittime;
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
			position.property("lff", lff++);
		}
		if (mode == Mode.FF) {
			Integer ff = (Integer) position.property("ff").value();
			position.property("ff", ff++);
		}
	}

	public boolean isInAPlace() {
		return Objects.equals(position.property("typ").value(), "plats");
	}

	public Mode getMode() {
		return mode;
	}

}
