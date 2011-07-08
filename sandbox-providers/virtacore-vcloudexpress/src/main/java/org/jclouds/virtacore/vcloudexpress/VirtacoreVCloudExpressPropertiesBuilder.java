/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.virtacore.vcloudexpress;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;

import java.util.Properties;

import org.jclouds.vcloud.VCloudPropertiesBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class VirtacoreVCloudExpressPropertiesBuilder extends VCloudPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-VA,US-CA");
      properties.setProperty(PROPERTY_ENDPOINT, "https://vcloud.virtacore.com/api");
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_NETWORK, ".*-Public");
      return properties;
   }

   public VirtacoreVCloudExpressPropertiesBuilder(Properties properties) {
      super(properties);
   }
}
