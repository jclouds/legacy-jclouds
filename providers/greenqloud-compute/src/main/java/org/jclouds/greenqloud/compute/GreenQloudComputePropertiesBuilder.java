package org.jclouds.greenqloud.compute;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.aws.ec2.reference.AWSEC2Constants.PROPERTY_EC2_CC_REGIONS;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.aws.ec2.AWSEC2PropertiesBuilder;

public class GreenQloudComputePropertiesBuilder extends AWSEC2PropertiesBuilder {

	public GreenQloudComputePropertiesBuilder(Properties properties) {
		super(properties);
	}

	@Override
	protected Properties defaultProperties() {
		Properties properties = super.defaultProperties();

		properties.setProperty(PROPERTY_ISO3166_CODES, "IS-1");
		properties.setProperty(PROPERTY_ENDPOINT, "https://api.greenqloud.com");
		properties.setProperty(PROPERTY_EC2_CC_REGIONS, "is-1");
		properties.setProperty(PROPERTY_REGIONS, "is-1");
		return properties;
	}

	public GreenQloudComputePropertiesBuilder() {
		super();
	}
}
