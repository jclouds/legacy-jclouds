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

package org.jclouds.rimuhosting.miro;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;
import static org.jclouds.rimuhosting.miro.reference.RimuHostingConstants.PROPERTY_RIMUHOSTING_DEFAULT_DC;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in RimuHosting Clients
 * 
 * @author Adrian Cole
 */
public class RimuHostingPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ZONES, "DCAUCKLAND,DCLONDON,DCDALLAS,DCSYDNEY");
      properties.setProperty(PROPERTY_ISO3166_CODES, "NZ-AUK,US-TX,AU-NSW,GB-LND");
      properties.setProperty(PROPERTY_ZONE + ".DCAUCKLAND." + ISO3166_CODES, "NZ-AUK");
      properties.setProperty(PROPERTY_ZONE + ".DCLONDON." + ISO3166_CODES, "GB-LND");
      properties.setProperty(PROPERTY_ZONE + ".DCDALLAS." + ISO3166_CODES, "US-TX");
      properties.setProperty(PROPERTY_ZONE + ".DCSYDNEY." + ISO3166_CODES, "AU-NSW");
      properties.setProperty(PROPERTY_API_VERSION, "TODO");
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.rimuhosting.com/r");
      properties.setProperty(PROPERTY_RIMUHOSTING_DEFAULT_DC, "DCDALLAS");
      return properties;
   }

   public RimuHostingPropertiesBuilder(Properties properties) {
      super(properties);
   }
}
