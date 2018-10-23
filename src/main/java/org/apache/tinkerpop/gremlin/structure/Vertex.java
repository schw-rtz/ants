package org.apache.tinkerpop.gremlin.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterators;

public class Vertex {

	private Map<String, Property> properties = new HashMap<>();
	private Set<Edge> incomingEdges = new HashSet<>();
	private Set<Edge> outgoingEdges = new HashSet<>();
	private int id;

	public Vertex(int id) {
		this.id = id;
	}

	public Property property(String string) {
		return properties.get(string);
	}

	public void property(String string, Object value) {
		properties.put(string, new Property(value));
	}

	public Iterator<Edge> edges(Direction aDirection) {
		if (aDirection == Direction.IN) {
			return incomingEdges.iterator();
		} else if (aDirection == Direction.OUT) {
			return outgoingEdges.iterator();
		} else if (aDirection == Direction.BOTH) {
			return Iterators.concat(edges(Direction.IN), edges(Direction.OUT));
		} else {
			throw new RuntimeException("dead code");
		}
	}

	public void addEdge(String label, Vertex vertex) {
		Edge e = new Edge(label, this, vertex);
		this.outgoingEdges.add(e);
		vertex.incomingEdges.add(e);
	}
	
	

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Vertex [id=" + id + ", properties=" + properties + "]";
	}

}
