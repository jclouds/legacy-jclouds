package org.jclouds.greenqloud.storage;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.aws.s3.AWSS3PropertiesBuilder;
import org.jclouds.location.reference.LocationConstants;

public class GreenQloudStoragePropertiesBuilder extends AWSS3PropertiesBuilder {

	@Override
	protected Properties defaultProperties() {
		Properties properties = super.defaultProperties();

		properties.setProperty(PROPERTY_ISO3166_CODES, "IS-1");
		properties.setProperty(PROPERTY_ENDPOINT, "https://s.greenqloud.com");
		properties.setProperty(PROPERTY_REGIONS, "is-1");
		properties.setProperty(PROPERTY_REGION + ".is-1." + ENDPOINT, "https://s.greenqloud.com");
		properties.setProperty("greenqloud-storage" + "." + LocationConstants.ENDPOINT, "https://s.greenqloud.com");
		return properties;
	}

	public GreenQloudStoragePropertiesBuilder() {
		super();
	}

	public GreenQloudStoragePropertiesBuilder(Properties properties) {
		super(properties);
	}

}
