package org.jclouds.aws.s3;

import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_REGIONS;

import java.util.Properties;

/**
 * Builds properties used in Walrus Clients
 * 
 * @author Adrian Cole
 */
public class WalrusPropertiesBuilder extends S3PropertiesBuilder {
   @Override
   protected Properties addEndpointProperties(Properties properties) {
      properties.setProperty(PROPERTY_S3_REGIONS, "Walrus");
      properties.setProperty(PROPERTY_S3_ENDPOINT,
            "http://173.205.188.130:8773/services/Walrus");
      properties.setProperty(PROPERTY_S3_ENDPOINT + ".Walrus",
            "http://173.205.188.130:8773/services/Walrus");
      return properties;
   }

   public WalrusPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public WalrusPropertiesBuilder(String id, String secret) {
      super(id, secret);
   }
}
