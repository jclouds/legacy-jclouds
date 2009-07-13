package org.jclouds.rackspace.cloudfiles.internal;

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;
import org.jclouds.rackspace.cloudfiles.CloudFilesConnection;
import org.jclouds.rackspace.cloudfiles.CloudFilesContext;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses a Guice Injector to configure the objects served by CloudFilesContext methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceCloudFilesContext implements CloudFilesContext {

   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;

   @Inject
   private GuiceCloudFilesContext(Injector injector, Closer closer) {
      this.injector = injector;
      this.closer = closer;
   }

   /**
    * {@inheritDoc}
    */
   public CloudFilesConnection getConnection() {
      return injector.getInstance(CloudFilesConnection.class);
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
