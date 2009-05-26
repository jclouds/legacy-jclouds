package org.jclouds.aws.codegen.model;

import java.util.List;

public class Package {
	private String name;
	private List<Command> commands;

	@Override
	public String toString() {
		return String.format("{'name':'%1$s', 'commands':%2$s", name, commands);
	}
}
