package org.jclouds.snia.cdmi.v1.queryparams;



import java.util.HashMap;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Generate CDMI object query parameters 
 * Note:  The preferred implementation would use jax-rs queryParam.
 * However, the CDMI query parameters specification does not conform to 
 * jax-rs queryParam of key=value separated by &.
 * Rather it follows the form:
 * ?<fieldname>;<fieldname>;.... 
 * ?metadata:<prefix>;...
 * ?children:<from>-<to>;...
 * ?value:<from>-<to>;...
 * 
 * @author Kenneth Nagin
 */
public class CDMIObjectQueryParams  {	
	
	protected String queryParams = "";
	
	public CDMIObjectQueryParams() {
		super();
	}

	/**
	 * Get CDMI data object's field value
	 * @param fieldname
	 * @return this
	 */
	public CDMIObjectQueryParams field(String fieldname) {
		queryParams = queryParams + fieldname + ";";
		return this;	
	}
	
	/**
	 * Get CDMI data object's metadata
	 * @return this
	 */
	public CDMIObjectQueryParams metadata() {
		queryParams = queryParams + "metadata;";
		return this;
	}

	/**
	 * Get CDMI data object's metadata associated with prefix
	 * @param prefix
	 * @return this
	 */
	public CDMIObjectQueryParams metadata(String prefix) {
		queryParams = queryParams + "metadata:"+prefix+";";
		return this;
	}
	

	public static class Builder {
		public static CDMIObjectQueryParams field(
				String fieldname) {
			CDMIObjectQueryParams options = new CDMIObjectQueryParams();
			return (CDMIObjectQueryParams) options.field(fieldname);
		}
		public static CDMIObjectQueryParams metadata(
				String prefix) {
			CDMIObjectQueryParams options = new CDMIObjectQueryParams();
			return (CDMIObjectQueryParams) options.metadata(prefix);
		}

	}
	
	public String toString () {
		return queryParams;
	}
	
}
