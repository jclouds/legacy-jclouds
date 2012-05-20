package org.jclouds.snia.cdmi.v1.options;

import java.util.Map;
import java.util.Map.Entry;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.snia.cdmi.v1.reference.CDMIHeaders;

/**
 * Contains options supported in the REST API for the CREATE container
 * operation. <h2>
 * 
 * @author Kenneth Nagin
 */
public class CreateDataObjectNonCDMIOptions extends BaseHttpRequestOptions {
	/**
	 * A name-value pair to associate with the container as metadata.
	 */
	public CreateDataObjectNonCDMIOptions withStringPayload(String value) {
		this.payload = value;
		return this;
	}
	

	

	public static class Builder {
		public static CreateDataObjectNonCDMIOptions withStringPayload(
				String value) {
			CreateDataObjectNonCDMIOptions options = new CreateDataObjectNonCDMIOptions();
			return (CreateDataObjectNonCDMIOptions) options.withStringPayload(value);
		}
		

	}
}
