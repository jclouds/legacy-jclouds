package org.jclouds.gq.compute;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_AMI_QUERY;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_REGIONS;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_GENERATE_INSTANCE_NAMES;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;

import java.util.Properties;

public class GQComputePropertiesBuilder extends org.jclouds.ec2.EC2PropertiesBuilder {

	public GQComputePropertiesBuilder(Properties properties) {
		super(properties);
	}

	@Override
	protected Properties defaultProperties() {
		Properties properties = super.defaultProperties();

//		properties.setProperty(PROPERTY_ISO3166_CODES, "IS-1");
		properties.setProperty(PROPERTY_ENDPOINT, "https://api.greenqloud.com");

		// sometimes, like in ec2, stop takes a very long time, perhaps
		// due to volume management. one example spent 2 minutes moving
		// from stopping->stopped state on an ec2 micro
		properties
				.setProperty(PROPERTY_TIMEOUT_NODE_SUSPENDED, 120 * 1000 + "");
		// auth fail sometimes happens in EC2, as the rc.local script that
		// injects the
		// authorized key executes after ssh has started.
		properties.setProperty("jclouds.ssh.max-retries", "7");
		properties.setProperty("jclouds.ssh.retry-auth", "true");
		properties.setProperty(PROPERTY_ENDPOINT,
				"https://ec2.us-east-1.amazonaws.com");
		properties.setProperty(PROPERTY_EC2_GENERATE_INSTANCE_NAMES, "true");
		//properties.putAll(Region.regionProperties());
		properties.remove(PROPERTY_EC2_AMI_OWNERS);
		// amazon, alestic, canonical, and rightscale
		properties
				.setProperty(
						PROPERTY_EC2_AMI_QUERY,
						"owner-id=137112412989,063491364108,099720109477,411009282317;state=available;image-type=machine");
		// amis that work with the cluster instances
		properties.setProperty(PROPERTY_EC2_CC_REGIONS, "is-1");
		properties
				.setProperty(
						PROPERTY_EC2_CC_AMI_QUERY,
						"virtualization-type=hvm;architecture=x86_64;owner-id=137112412989,099720109477;hypervisor=xen;state=available;image-type=machine;root-device-type=ebs"); 
		return properties;
	}

	public GQComputePropertiesBuilder() {
		super();
	}
}
