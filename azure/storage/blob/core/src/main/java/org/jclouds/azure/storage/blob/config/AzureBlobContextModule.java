package org.jclouds.azure.storage.blob.config;

import org.jclouds.azure.storage.blob.AzureBlobConnection;
import org.jclouds.azure.storage.blob.AzureBlobContext;
import org.jclouds.azure.storage.blob.internal.GuiceAzureBlobContext;

import com.google.inject.AbstractModule;

/**
 * Configures the {@link AzureBlobContext}; requires {@link AzureBlobConnection} bound.
 * 
 * @author Adrian Cole
 */
public class AzureBlobContextModule extends AbstractModule {

   @Override
   protected void configure() {
      this.requireBinding(AzureBlobConnection.class);
      bind(AzureBlobContext.class).to(GuiceAzureBlobContext.class);
   }

}
