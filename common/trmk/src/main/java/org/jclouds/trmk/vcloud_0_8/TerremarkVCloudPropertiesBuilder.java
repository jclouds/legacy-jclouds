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
package org.jclouds.trmk.vcloud_0_8;

import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.util.Properties;


/**
 * Builds properties used in Terremark VCloud Clients
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudPropertiesBuilder extends VCloudExpressPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "15");
      // with ssh key injection comes another reboot. allowing more time
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, 360l * 1000l + "");
      return properties;
   }

   public TerremarkVCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

   @Override
   public Properties build() {
      setExtensions();
      return super.build();
   }

   void setExtensions() {
      if (properties.getProperty(PROPERTY_TERREMARK_EXTENSION_NS) == null) {
         properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NS, String.format("urn:tmrk:%s-%s",
                  properties.getProperty(PROPERTY_TERREMARK_EXTENSION_NAME), properties
                           .getProperty(PROPERTY_TERREMARK_EXTENSION_VERSION)));
      }
   }
}
