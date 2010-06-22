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
package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_API;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_VERSION_SCHEMA;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_NAMESPACE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_XML_SCHEMA;

import java.net.URI;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.vcloud.domain.FenceMode;

/**
 * Builds properties used in VCloud Clients
 * 
 * @author Adrian Cole
 */
public class VCloudPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_VCLOUD_VERSION_API, "0.8");
      properties.setProperty(PROPERTY_VCLOUD_VERSION_SCHEMA, "0.8");
      properties.setProperty(PROPERTY_VCLOUD_SESSIONINTERVAL, 8 * 60 + "");
      properties.setProperty(PROPERTY_VCLOUD_XML_SCHEMA,
            "http://vcloud.safesecureweb.com/ns/vcloud.xsd");
      properties.setProperty("jclouds.dns_name_length_min", "1");
      properties.setProperty("jclouds.dns_name_length_max", "80");
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED,
            180l * 1000l + "");
      return properties;
   }

   public VCloudPropertiesBuilder(Properties properties) {
      super(properties);
      setNs();
      setFenceMode();
   }

   protected void setNs() {
      if (properties.getProperty(PROPERTY_VCLOUD_XML_NAMESPACE) == null)
         properties.setProperty(PROPERTY_VCLOUD_XML_NAMESPACE,
               "http://www.vmware.com/vcloud/v"
                     + properties.getProperty(PROPERTY_VCLOUD_VERSION_SCHEMA));
   }

   protected void setFenceMode() {
      if (properties.getProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) == null) {
         if (properties.getProperty(PROPERTY_VCLOUD_VERSION_SCHEMA).startsWith(
               "0.8"))
            properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE,
                  "allowInOut");
         else
            properties.setProperty(PROPERTY_VCLOUD_DEFAULT_FENCEMODE,
                  FenceMode.BRIDGED);
      }
   }

   public VCloudPropertiesBuilder(URI endpoint, String id, String secret) {
      super();
      setNs();
      setFenceMode();
      withCredentials(id, secret);
      withEndpoint(endpoint);
   }

   public VCloudPropertiesBuilder withTokenExpiration(long seconds) {
      properties.setProperty(PROPERTY_VCLOUD_SESSIONINTERVAL, seconds + "");
      return this;
   }

   public VCloudPropertiesBuilder withCredentials(String id, String secret) {
      properties.setProperty(PROPERTY_VCLOUD_USER, checkNotNull(id, "user"));
      properties.setProperty(PROPERTY_VCLOUD_KEY, checkNotNull(secret, "key"));
      return this;
   }

   public VCloudPropertiesBuilder withEndpoint(URI endpoint) {
      properties.setProperty(PROPERTY_VCLOUD_ENDPOINT, checkNotNull(endpoint,
            "endpoint").toString());
      return this;
   }
}
