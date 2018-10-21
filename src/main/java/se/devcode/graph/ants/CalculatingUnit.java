package se.devcode.graph.ants;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class CalculatingUnit {

	private Vertex vertex;
	private Direction direction;
	private Edge edge;
	private Integer lff;
	private Integer ff;

	public CalculatingUnit(Edge edge, Direction direction) {
		if (direction == direction.OUT) {
			this.vertex = edge.outVertex();
		} else {
			vertex = edge.inVertex();
		}

		this.direction = direction;
		this.edge = edge;

		lff = (Integer) vertex.property("lff").value();
		ff = (Integer) vertex.property("ff").value();
	}

	public Vertex getVertex() {
		return vertex;
	}

	public Direction getDirection() {
		return direction;
	}

	public Edge getEdge() {
		return edge;
	}

	public Integer getLff() {
		return lff;
	}

	public Integer getFf() {
		return ff;
	}

	public void modifyProbabilities(Mode mode) {
		if (mode == Mode.LFF)
		{
			
		}
	}
	
	

}
