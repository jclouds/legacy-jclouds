package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_DEFAULT_REGIONS;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_REGIONS;

import java.util.Properties;

/**
 * Builds properties used in Walrus Clients
 * 
 * @author Adrian Cole
 */
public class WalrusPropertiesBuilder extends S3PropertiesBuilder {
   @Override
   protected Properties addEndpoints(Properties properties) {
      properties.setProperty(PROPERTY_REGIONS, "Walrus");
      properties.setProperty(PROPERTY_DEFAULT_REGIONS, "Walrus");
      properties.setProperty(PROPERTY_ENDPOINT, "http://ecc.eucalyptus.com:8773/services/Walrus");
      properties.setProperty(PROPERTY_ENDPOINT + ".Walrus",
               "http://ecc.eucalyptus.com:8773/services/Walrus");
      return properties;
   }

   public WalrusPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
