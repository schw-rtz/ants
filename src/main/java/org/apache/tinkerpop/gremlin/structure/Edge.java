package org.apache.tinkerpop.gremlin.structure;

public class Edge {

	private Vertex outVertex;
	private Vertex inVertex;
	private String label;

	/**
	 * @param label
	 * @param outVertex
	 *            also known as from
	 * @param inVertex
	 *            also know as to
	 */
	public Edge(String label, Vertex outVertex, Vertex inVertex) {
		this.label = label;
		this.outVertex = outVertex;
		this.inVertex = inVertex;
	}

	public Vertex outVertex() {
		return outVertex;
	}

	public Vertex inVertex() {
		return inVertex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inVertex == null) ? 0 : inVertex.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((outVertex == null) ? 0 : outVertex.hashCode());
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
		Edge other = (Edge) obj;
		if (inVertex == null) {
			if (other.inVertex != null)
				return false;
		} else if (!inVertex.equals(other.inVertex))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (outVertex == null) {
			if (other.outVertex != null)
				return false;
		} else if (!outVertex.equals(other.outVertex))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [label=" + label + ", outVertex=" + outVertex.getId() + ", inVertex=" + inVertex.getId() + "]";
	}

}
