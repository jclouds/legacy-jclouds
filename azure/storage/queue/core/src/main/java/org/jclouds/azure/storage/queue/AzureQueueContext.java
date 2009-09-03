package org.jclouds.azure.storage.queue;

import org.jclouds.cloud.CloudContext;

/**
 * Represents an authenticated context to Azure Queue Service.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @see AzureQueueConnection
 * @see CloudContext
 * @author Adrian Cole
 * 
 */
public interface AzureQueueContext extends CloudContext<AzureQueueConnection> {

}