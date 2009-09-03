package org.jclouds.azure.storage.blob.internal;

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.azure.storage.blob.AzureBlobConnection;
import org.jclouds.azure.storage.blob.AzureBlobContext;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses a Guice Injector to configure the objects served by AzureBlobContext methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceAzureBlobContext implements AzureBlobContext {

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;

   @Inject
   private GuiceAzureBlobContext(Injector injector, Closer closer) {
      this.injector = injector;
      this.closer = closer;
   }

   /**
    * {@inheritDoc}
    */
   public AzureBlobConnection getConnection() {
      return injector.getInstance(AzureBlobConnection.class);
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
