package com.thinkaurelius.titan.core;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tinkerpop.gremlin.structure.Vertex;

public class TitanGraph {

	private ArrayList<Vertex> vertices = new ArrayList<>();

	public Iterator<Vertex> vertices() {
		return vertices.iterator();
	}

	public synchronized TitanVertex addVertex() {
		int id = vertices.size();
		TitanVertex e = new TitanVertex(id);
		vertices.add(e);
		return e;
	}

	@Override
	public String toString() {
		return "TitanGraph [vertices=" + vertices + "]";
	}

	
	
}
