package org.apache.tinkerpop.gremlin.structure;

public class Property {

	private Object value;

	public Property(Object value2) {
		value = value2;
	}

	public Object value() {
		return value;
	}

	@Override
	public String toString() {
		return "P[" + value + "]";
	}

	
}
