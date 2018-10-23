package com.thinkaurelius.titan.core;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class TitanVertex extends Vertex {

	public TitanVertex(int id) {
		super(id);
	}

	public void addEdge(String label, Vertex vertex) {
		 super.addEdge (label, vertex);
	}

}
