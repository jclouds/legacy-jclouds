package org.jclouds.snia.cdmi.v1.options;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for the CREATE container
 * operation. <h2>
 * 
 * @author Kenneth Nagin
 */
public class CreateContainerOptions extends BaseHttpRequestOptions {
	/**
	 * A name-value pair to associate with the container as metadata.
	 */
	public CreateContainerOptions withMetadata(Map<String, String> metadata) {
		String s = "{ \"metadata\" : {\"key1\" : \"value1\",\"key2\" : \"value2\"} }";
		this.payload = s;
		String payload = "{ \"metadata\" : {";
		String separator = " ";
		for (Entry<String, String> entry : metadata.entrySet()) {
			payload = payload + separator + "\"" + entry.getKey() + "\" : \""
					+ entry.getValue() + "\"";
			separator = ",";
		}
		this.payload = payload + "} }";
		return this;
	}

	public static class Builder {
		public static CreateContainerOptions withMetadata(
				Map<String, String> metadata) {
			CreateContainerOptions options = new CreateContainerOptions();
			return (CreateContainerOptions) options.withMetadata(metadata);
		}
	}
}
