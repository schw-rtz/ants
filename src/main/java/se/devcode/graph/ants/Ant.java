package se.devcode.graph.ants;

import java.util.Objects;

import com.thinkaurelius.titan.core.TitanVertex;

public class Ant {

	private TitanVertex position;
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

	public TitanVertex getPosition() {
		return position;
	}
	
	

	public void setPosition(TitanVertex position) {
		this.position = position;
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
