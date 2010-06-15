package org.jclouds.aws.s3;

import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_AUTH_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_DEFAULT_REGIONS;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_HEADER_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_REGIONS;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SERVICE_EXPR;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

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
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-goog-meta-");
      properties.setProperty(PROPERTY_S3_AUTH_TAG, "GOOG1");
      properties.setProperty(PROPERTY_S3_HEADER_TAG, "goog");
      properties.setProperty(PROPERTY_S3_SERVICE_EXPR,
            "\\.commondatastorage\\.googleapis\\.com");
      return properties;
   }

   @Override
   protected Properties addEndpoints(Properties properties) {
      properties.setProperty(PROPERTY_S3_REGIONS, "GoogleStorage");
      properties.setProperty(PROPERTY_S3_DEFAULT_REGIONS, "GoogleStorage");
      properties.setProperty(PROPERTY_S3_ENDPOINT,
            "https://commondatastorage.googleapis.com");
      properties.setProperty(PROPERTY_S3_ENDPOINT + ".GoogleStorage",
            "https://commondatastorage.googleapis.com");
      return properties;
   }

   public GoogleStoragePropertiesBuilder(Properties properties) {
      super(properties);
   }

   public GoogleStoragePropertiesBuilder(String id, String secret) {
      super(id, secret);
   }
}
