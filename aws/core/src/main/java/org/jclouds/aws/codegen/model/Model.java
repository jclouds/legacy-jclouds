package org.jclouds.aws.codegen.model;

import java.util.List;

public class Model {
	private List<Package> packages;
	
	@Override
	public String toString() {
		return packages.toString();
	}
}
