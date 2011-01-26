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

package org.jclouds.openstack.swift;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.openstack.OpenStackAuthAsyncClient;

/**
 * Builds properties used in CloudFiles Connections
 * 
 * @author Adrian Cole
 */
public class SwiftPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_REGIONS, "DEFAULT");
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "X-Object-Meta-");
      properties.setProperty(PROPERTY_API_VERSION, OpenStackAuthAsyncClient.VERSION);
      return properties;
   }

   public SwiftPropertiesBuilder(Properties properties) {
      super(properties);
   }

   protected SwiftPropertiesBuilder withMetaPrefix(String prefix) {
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, prefix);
      return this;
   }
}
