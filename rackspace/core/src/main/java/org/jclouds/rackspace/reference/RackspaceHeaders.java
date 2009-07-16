package org.jclouds.rackspace.reference;

/**
 * Headers common to Rackspace apis.
 * 
 * @see <a href="http://docs.rackspacecloud.com/servers/api/cs-devguide-latest.pdf" />
 * @author Adrian Cole
 * 
 */
public interface RackspaceHeaders {

   public static final String AUTH_USER = "X-Auth-User";
   public static final String AUTH_KEY = "X-Auth-Key";
   public static final String AUTH_TOKEN = "X-Auth-Token";
   public static final String CDN_MANAGEMENT_URL = "X-CDN-Management-Url";
   public static final String SERVER_MANAGEMENT_URL = "X-Server-Management-Url";
   public static final String STORAGE_URL = "X-Storage-Url";

}