package org.jclouds.aws.codegen.model;

import java.util.List;

public class Command {
	private String className;
	private String packageName;
	private String awsType;
	private List<Parameter> parameters;
	private Handler handler;
	private Response response;
	private List<String> see;
}
