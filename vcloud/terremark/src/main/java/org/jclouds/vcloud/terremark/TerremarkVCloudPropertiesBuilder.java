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
package org.jclouds.vcloud.terremark;

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_API;

import java.net.URI;
import java.util.Properties;

import org.jclouds.vcloud.VCloudPropertiesBuilder;

/**
 * Builds properties used in Terremark VCloud Clients
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudPropertiesBuilder extends VCloudPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_VCLOUD_VERSION_API, "0.8a-ext1.6");
      properties.setProperty(PROPERTY_VCLOUD_ENDPOINT,
            "https://services.vcloudexpress.terremark.com/api");
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "15");
      return properties;
   }

   public TerremarkVCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public TerremarkVCloudPropertiesBuilder(String id, String secret) {
      super(URI.create("https://services.vcloudexpress.terremark.com/api"), id,
            secret);
   }
}
