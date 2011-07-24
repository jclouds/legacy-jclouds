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

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NS;
import static org.jclouds.trmk.vcloud_0_8.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_SCHEMA;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.trmk.vcloud_0_8.domain.FenceMode;

/**
 * Builds properties used in Terremark VCloud Clients
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "0.8");
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "0.8");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 8 * 60 + "");
      properties.setProperty(PROPERTY_VCLOUD_XML_SCHEMA, "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "15");
      // with ssh key injection comes another reboot. allowing more time
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, 360l * 1000l + "");
      return properties;
   }

   public TerremarkVCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

   void setExtensions() {
      if (properties.getProperty(PROPERTY_TERREMARK_EXTENSION_NS) == null) {
         properties.setProperty(
               PROPERTY_TERREMARK_EXTENSION_NS,
               String.format("urn:tmrk:%s-%s", properties.getProperty(PROPERTY_TERREMARK_EXTENSION_NAME),
                     properties.getProperty(PROPERTY_TERREMARK_EXTENSION_VERSION)));
      }
   }

   protected void setNs() {
      if (properties.getProperty(PROPERTY_VCLOUD_XML_NAMESPACE) == null)
         properties.setProperty(PROPERTY_VCLOUD_XML_NAMESPACE,
               "http://www.vmware.com/vcloud/v" + properties.getProperty(PROPERTY_VCLOUD_VERSION_SCHEMA));
   }

   protected void setFenceMode() {
      if (properties.getProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) == null) {
         if (properties.getProperty(PROPERTY_VCLOUD_VERSION_SCHEMA).startsWith("0.8"))
            properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE, "allowInOut");
         else
            properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE, FenceMode.ALLOW_IN_OUT.toString());
      }
   }

   public TerremarkVCloudPropertiesBuilder withApiVersion(String version) {
      properties.setProperty(PROPERTY_API_VERSION, "0.8");
      return this;
   }

   public TerremarkVCloudPropertiesBuilder withSchemaVersion(String version) {
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "0.8");
      return this;
   }

   @Override
   public Properties build() {
      setNs();
      setFenceMode();
      setExtensions();
      return super.build();
   }
}
