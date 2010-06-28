/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.s3;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_DEFAULT_REGIONS;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_REGIONS;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_AUTH_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_HEADER_TAG;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SERVICE_EXPR;
import static org.jclouds.blobstore.reference.BlobStoreConstants.DIRECTORY_SUFFIX_FOLDER;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.aws.domain.Region;

import com.google.common.base.Joiner;

/**
 * Builds properties used in S3 Connections
 * 
 * @author Adrian Cole
 */
public class S3PropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, S3AsyncClient.VERSION);
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-amz-meta-");
      properties.setProperty(PROPERTY_S3_AUTH_TAG, "AWS");
      properties.setProperty(PROPERTY_S3_HEADER_TAG, "aws");
      properties.setProperty(PROPERTY_S3_SERVICE_EXPR, "\\.s3[^.]*\\.amazonaws\\.com");
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      addEndpoints(properties);
      properties.setProperty(PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX, DIRECTORY_SUFFIX_FOLDER);
      return properties;
   }

   protected Properties addEndpoints(Properties properties) {
      properties.setProperty(PROPERTY_REGIONS, Joiner.on(',').join(Region.US_STANDARD,
               Region.US_EAST_1, Region.US_WEST_1, "EU", Region.AP_SOUTHEAST_1));
      properties.setProperty(PROPERTY_DEFAULT_REGIONS, Joiner.on(',').join(Region.US_STANDARD,
               Region.US_EAST_1));
      properties.setProperty(PROPERTY_ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_ENDPOINT + "." + Region.US_STANDARD,
               "https://s3.amazonaws.com");
      properties
               .setProperty(PROPERTY_ENDPOINT + "." + Region.US_EAST_1, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_ENDPOINT + "." + Region.US_WEST_1,
               "https://s3-us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_ENDPOINT + "." + "EU", "https://s3-eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_ENDPOINT + "." + Region.AP_SOUTHEAST_1,
               "https://s3-ap-southeast-1.amazonaws.com");
      return properties;
   }

   public S3PropertiesBuilder(Properties properties) {
      super(properties);
   }

   public S3PropertiesBuilder() {
      super();
   }

   protected S3PropertiesBuilder withMetaPrefix(String prefix) {
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, prefix);
      return this;
   }
}
