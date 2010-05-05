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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.Constants.PROPERTY_RELAX_HOSTNAME;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT_AP_SOUTHEAST_1;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT_EU_WEST_1;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT_US_STANDARD;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_ENDPOINT_US_WEST_1;
import static org.jclouds.aws.s3.reference.S3Constants.PROPERTY_S3_SESSIONINTERVAL;
import static org.jclouds.blobstore.reference.BlobStoreConstants.DIRECTORY_SUFFIX_FOLDER;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.aws.s3.reference.S3Constants;

/**
 * Builds properties used in S3 Connections
 * 
 * @author Adrian Cole
 */
public class S3PropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_RELAX_HOSTNAME, "true");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "x-amz-meta-");
      properties.setProperty(PROPERTY_S3_ENDPOINT, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_S3_ENDPOINT_US_STANDARD, "https://s3.amazonaws.com");
      properties.setProperty(PROPERTY_S3_ENDPOINT_US_WEST_1, "https://s3-us-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_S3_ENDPOINT_EU_WEST_1, "https://s3-eu-west-1.amazonaws.com");
      properties.setProperty(PROPERTY_S3_ENDPOINT_AP_SOUTHEAST_1,
               "https://s3-ap-southeast-1.amazonaws.com");
      properties.setProperty(PROPERTY_S3_SESSIONINTERVAL, "60");
      properties.setProperty(PROPERTY_BLOBSTORE_DIRECTORY_SUFFIX, DIRECTORY_SUFFIX_FOLDER);
      return properties;
   }

   public S3PropertiesBuilder(Properties properties) {
      super(properties);
   }

   public S3PropertiesBuilder(String id, String secret) {
      super();
      withCredentials(id, secret);
   }

   @Override
   public S3PropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_AWS_ACCESSKEYID, checkNotNull(id, "awsAccessKeyId"));
      properties.setProperty(PROPERTY_AWS_SECRETACCESSKEY, checkNotNull(secret,
               "awsSecretAccessKey"));
      return this;
   }

   public S3PropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(S3Constants.PROPERTY_S3_ENDPOINT, checkNotNull(endpoint, "endpoint")
               .toString());
      return this;
   }

   public S3PropertiesBuilder withTimeStampExpiration(long seconds) {
      properties.setProperty(PROPERTY_S3_SESSIONINTERVAL, seconds + "");
      return this;
   }

   protected S3PropertiesBuilder withMetaPrefix(String prefix) {
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, prefix);
      return this;
   }
}
