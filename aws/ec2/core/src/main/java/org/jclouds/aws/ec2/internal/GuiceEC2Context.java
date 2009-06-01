package org.jclouds.aws.ec2.internal;

import java.io.IOException;

import javax.annotation.Resource;

import org.jclouds.aws.ec2.EC2Connection;
import org.jclouds.aws.ec2.EC2Context;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Uses a Guice Injector to configure the objects served by EC2Context methods.
 * 
 * @author Adrian Cole
 * @see Injector
 */
public class GuiceEC2Context implements EC2Context {
	
   @Resource
   private Logger logger = Logger.NULL;
   private final Injector injector;
   private final Closer closer;

   @Inject
   private GuiceEC2Context(Injector injector, Closer closer) {
      this.injector = injector;
      this.closer = closer;
   }

   /**
    * {@inheritDoc}
    */
   public EC2Connection getConnection() {
      return injector.getInstance(EC2Connection.class);
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
