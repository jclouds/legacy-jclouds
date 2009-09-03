package org.jclouds.azure.storage.reference;

/**
 * Additional headers specified by Azure Storage REST API.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179357.aspx" />
 * @author Adrian Cole
 * 
 */
public interface AzureStorageHeaders {

   public static final String USER_METADATA_PREFIX = "x-ms-meta-";
   public static final String REQUEST_ID = "x-ms-request-id";
   public static final String VERSION = "x-ms-version";

}