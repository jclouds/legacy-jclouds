package org.jclouds.snia.cdmi.v1.options;

import java.util.Map;

/**
 * Optional Create CDMI Contain options
 * 
 * @author Kenneth Nagin
 */
public class CreateContainerOptions extends CreateCDMIObjectOptions {
	/**
	 * A name-value pair to associate with the container as metadata.
	 */
	public CreateContainerOptions metadata(Map<String, String> metadata) {
		super.metadata(metadata);
		return this;
		
	}
	public static class Builder {
		public static CreateContainerOptions metadata(
				Map<String, String> metadata) {
			CreateContainerOptions options = new CreateContainerOptions();
			return (CreateContainerOptions) options.metadata(metadata);
		}
	}
}
