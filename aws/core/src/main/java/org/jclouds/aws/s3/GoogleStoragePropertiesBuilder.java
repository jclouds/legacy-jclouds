/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AUTH_TAG;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_HEADER_TAG;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

/**
 * Builds properties used in Google Storage
 * 
 * @author Adrian Cole
 */
public class GoogleStoragePropertiesBuilder extends S3PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_AUTH_TAG, "GOOG1");
      properties.setProperty(PROPERTY_HEADER_TAG, "goog");
      return properties;
   }

   @Override
   protected Properties addEndpoints(Properties properties) {
      properties.setProperty(PROPERTY_REGIONS, "GoogleStorage");
      properties.setProperty(PROPERTY_ENDPOINT, "https://commondatastorage.googleapis.com");
      properties.setProperty(PROPERTY_ENDPOINT + ".GoogleStorage",
               "https://commondatastorage.googleapis.com");
      return properties;
   }

   public GoogleStoragePropertiesBuilder(Properties properties) {
      super(properties);
   }

}
