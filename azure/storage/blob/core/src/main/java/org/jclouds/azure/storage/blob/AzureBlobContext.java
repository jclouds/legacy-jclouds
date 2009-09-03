package org.jclouds.azure.storage.blob;

import org.jclouds.cloud.CloudContext;

/**
 * Represents an authenticated context to Cloud Files.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090311.pdf" />
 * @see AzureBlobConnection
 * @see CloudContext
 * @author Adrian Cole
 * 
 */
public interface AzureBlobContext extends CloudContext<AzureBlobConnection> {

}