package org.jclouds.azure.storage.queue.config;

import java.net.URI;

import org.jclouds.azure.storage.queue.AzureQueueConnection;
import org.jclouds.azure.storage.config.RestAzureStorageConnectionModule;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Configures the Azure Queue Service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestAzureQueueConnectionModule extends RestAzureStorageConnectionModule {
  
   @Provides
   @Singleton
   protected AzureQueueConnection provideAzureStorageConnection(URI uri, RestClientFactory factory) {
      return factory.create(uri, AzureQueueConnection.class);
   }

}