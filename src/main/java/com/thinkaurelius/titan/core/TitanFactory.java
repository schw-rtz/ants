package com.thinkaurelius.titan.core;

public class TitanFactory {

	public static TitanGraph open(String anyOldStringWillDo) {
		return new TitanGraph();
	}

}
