package org.jclouds.azure.management.compute.functions;

import org.jclouds.azure.management.domain.Deployment;
import org.jclouds.compute.domain.NodeMetadata;

import com.google.common.base.Function;

public class DeploymentToNodeMetadata implements Function<Deployment, NodeMetadata> {

	@Override
	public NodeMetadata apply(Deployment input) {
		return null;
	}

}
