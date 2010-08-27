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

package org.jclouds.vcloud;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_SCHEMA;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.vcloud.domain.network.FenceMode;

/**
 * Builds properties used in VCloud Clients
 * 
 * @author Adrian Cole
 */
public class VCloudPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "1.0");
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "1");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 8 * 60 + "");
      properties.setProperty(PROPERTY_VCLOUD_XML_SCHEMA, "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "80");
      properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE, FenceMode.BRIDGED.toString());
      // TODO integrate this with the {@link ComputeTimeouts} instead of having a single timeout for
      // everything.
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, 600l * 1000l + "");
      return properties;
   }

   public VCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

   protected void setNs() {
      if (properties.getProperty(PROPERTY_VCLOUD_XML_NAMESPACE) == null)
         properties.setProperty(PROPERTY_VCLOUD_XML_NAMESPACE, "http://www.vmware.com/vcloud/v"
                  + properties.getProperty(PROPERTY_VCLOUD_VERSION_SCHEMA));
   }

   public VCloudPropertiesBuilder withApiVersion(String version) {
      properties.setProperty(PROPERTY_API_VERSION, "1.0");
      return this;
   }

   public VCloudPropertiesBuilder withSchemaVersion(String version) {
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "1.0");
      return this;
   }

   @Override
   public Properties build() {
      setNs();
      return super.build();
   }
}
