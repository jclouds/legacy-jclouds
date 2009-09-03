package org.jclouds.azure.storage.queue.config;

import org.jclouds.azure.storage.queue.AzureQueueConnection;
import org.jclouds.azure.storage.queue.AzureQueueContext;
import org.jclouds.azure.storage.queue.internal.GuiceAzureQueueContext;

import com.google.inject.AbstractModule;

/**
 * Configures the {@link AzureQueueContext}; requires {@link AzureQueueConnection} bound.
 * 
 * @author Adrian Cole
 */
public class AzureQueueContextModule extends AbstractModule {

   @Override
   protected void configure() {
      this.requireBinding(AzureQueueConnection.class);
      bind(AzureQueueContext.class).to(GuiceAzureQueueContext.class);
   }

}
