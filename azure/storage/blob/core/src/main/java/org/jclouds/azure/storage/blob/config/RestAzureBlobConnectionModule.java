package org.jclouds.azure.storage.blob.config;

import java.net.URI;

import org.jclouds.azure.storage.blob.AzureBlobConnection;
import org.jclouds.azure.storage.config.RestAzureStorageConnectionModule;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.RestClientFactory;

import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Configures the Azure Blob Service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresCloudConnection
@RequiresHttp
public class RestAzureBlobConnectionModule extends RestAzureStorageConnectionModule {
  
   @Provides
   @Singleton
   protected AzureBlobConnection provideAzureStorageConnection(URI uri, RestClientFactory factory) {
      return factory.create(uri, AzureBlobConnection.class);
   }

}