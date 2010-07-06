package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_DEFAULT_REGIONS;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_REGIONS;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_AUTH_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_HEADER_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SERVICE_EXPR;

import java.util.Properties;

/**
 * Builds properties used in Walrus Clients
 * 
 * @author Adrian Cole
 */
public class GoogleStoragePropertiesBuilder extends S3PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_S3_AUTH_TAG, "GOOG1");
      properties.setProperty(PROPERTY_S3_HEADER_TAG, "goog");
      properties.setProperty(PROPERTY_S3_SERVICE_EXPR, "\\.commondatastorage\\.googleapis\\.com");
      return properties;
   }

   @Override
   protected Properties addEndpoints(Properties properties) {
      properties.setProperty(PROPERTY_REGIONS, "GoogleStorage");
      properties.setProperty(PROPERTY_DEFAULT_REGIONS, "GoogleStorage");
      properties.setProperty(PROPERTY_ENDPOINT, "https://commondatastorage.googleapis.com");
      properties.setProperty(PROPERTY_ENDPOINT + ".GoogleStorage",
               "https://commondatastorage.googleapis.com");
      return properties;
   }

   public GoogleStoragePropertiesBuilder(Properties properties) {
      super(properties);
   }

}
