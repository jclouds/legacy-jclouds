package org.jclouds.blobstore;

import java.net.URI;
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
      return properties;
   }

   public TransientBlobStorePropertiesBuilder(Properties properties) {
      super(properties);
   }

   public TransientBlobStorePropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   public TransientBlobStorePropertiesBuilder withCredentials(String id, String secret) {
      return this;
   }

   public TransientBlobStorePropertiesBuilder withEndpoint(URI endpoint) {
      return this;
   }
}
