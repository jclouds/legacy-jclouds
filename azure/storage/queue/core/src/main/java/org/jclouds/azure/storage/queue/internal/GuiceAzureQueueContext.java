package org.jclouds.azure.storage.queue.internal;

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.azure.storage.queue.AzureQueueConnection;
import org.jclouds.azure.storage.queue.AzureQueueContext;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses a Guice Injector to configure the objects served by AzureQueueContext methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceAzureQueueContext implements AzureQueueContext {

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;

   @Inject
   private GuiceAzureQueueContext(Injector injector, Closer closer) {
      this.injector = injector;
      this.closer = closer;
   }

   /**
    * {@inheritDoc}
    */
   public AzureQueueConnection getConnection() {
      return injector.getInstance(AzureQueueConnection.class);
   }


   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

}
