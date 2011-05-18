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
package org.jclouds.savvis.vpdc;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.savvis.vpdc.reference.VPDCConstants.PROPERTY_VPDC_TIMEOUT_TASK_COMPLETED;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in Symphony VPDC Clients
 * 
 * @author Adrian Cole
 */
public class VPDCPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "1.0");
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.symphonyvpdc.savvis.net/vpdc");
      properties.setProperty(PROPERTY_VPDC_TIMEOUT_TASK_COMPLETED, 600l * 1000l + "");
      return properties;
   }

   public VPDCPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
