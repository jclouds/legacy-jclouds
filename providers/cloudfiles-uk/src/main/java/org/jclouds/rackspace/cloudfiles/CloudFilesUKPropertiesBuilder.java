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

package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.openstack.swift.SwiftPropertiesBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class CloudFilesUKPropertiesBuilder extends SwiftPropertiesBuilder {

   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_REGIONS, "UK");
      properties.setProperty(PROPERTY_ENDPOINT, "https://lon.auth.api.rackspacecloud.com");
      properties.setProperty(PROPERTY_ISO3166_CODES, "GB-SLG");
      return properties;
   }

   public CloudFilesUKPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
