package org.jclouds.blobstore;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in transient blobstores
 * 
 * @author Adrian Cole
 */
public class TransientBlobStorePropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "http://localhost/transient");
      properties.setProperty(PROPERTY_API_VERSION, "1");
      properties.setProperty(PROPERTY_IDENTITY, System.getProperty("user.name"));
      properties.setProperty(PROPERTY_USER_THREADS, "0");
      properties.setProperty(PROPERTY_IO_WORKER_THREADS, "0");
      return properties;
   }

   public TransientBlobStorePropertiesBuilder(Properties properties) {
      super(properties);
   }

}
