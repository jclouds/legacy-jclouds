package org.jclouds.azure.storage.blob;

import org.jclouds.cloud.CloudContext;

/**
 * Represents an authenticated context to Azure Blob Service.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @see AzureBlobConnection
 * @see CloudContext
 * @author Adrian Cole
 * 
 */
public interface AzureBlobContext extends CloudContext<AzureBlobConnection> {

}