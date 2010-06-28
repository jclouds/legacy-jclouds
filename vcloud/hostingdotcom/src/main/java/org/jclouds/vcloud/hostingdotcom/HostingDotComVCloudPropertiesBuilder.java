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
package org.jclouds.vcloud.hostingdotcom;

import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.util.Properties;

import org.jclouds.vcloud.VCloudPropertiesBuilder;

/**
 * Builds properties used in hosting.com VCloud Clients
 * 
 * @author Adrian Cole
 */
public class HostingDotComVCloudPropertiesBuilder extends VCloudPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "https://vcloud.safesecureweb.com/api");
      properties.setProperty(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED, 45 * 60 * 1000l + "");
      return properties;
   }

   public HostingDotComVCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
