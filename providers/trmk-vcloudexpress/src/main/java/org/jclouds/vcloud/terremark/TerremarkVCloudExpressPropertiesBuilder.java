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

package org.jclouds.vcloud.terremark;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.vcloud.terremark.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_NAME;
import static org.jclouds.vcloud.terremark.reference.TerremarkConstants.PROPERTY_TERREMARK_EXTENSION_VERSION;

import java.util.Properties;

/**
 * Builds properties used in Terremark VCloud Clients
 * 
 * @author Adrian Cole
 */
public class TerremarkVCloudExpressPropertiesBuilder extends TerremarkVCloudPropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "0.8a-ext1.6");
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-FL");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_NAME, "vCloudExpressExtensions");
      properties.setProperty(PROPERTY_TERREMARK_EXTENSION_VERSION, "1.6");
      properties.setProperty(PROPERTY_ENDPOINT, "https://services.vcloudexpress.terremark.com/api");
      return properties;
   }

   public TerremarkVCloudExpressPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
