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
package org.jclouds.ibm.smartcloud;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.compute.reference.ComputeServiceConstants.PROPERTY_TIMEOUT_NODE_RUNNING;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in IBMSmartCloud Clients
 * 
 * @author Adrian Cole
 */
public class IBMSmartCloudPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ZONES, "41,61,82,101,121,141");
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-NC,DE-BW,US-CO,CA-ON,JP-12,SG");
      properties.setProperty(PROPERTY_ZONE + ".41." + ISO3166_CODES, "US-NC");
      properties.setProperty(PROPERTY_ZONE + ".61." + ISO3166_CODES, "DE-BW");
      properties.setProperty(PROPERTY_ZONE + ".82." + ISO3166_CODES, "US-CO");
      properties.setProperty(PROPERTY_ZONE + ".101." + ISO3166_CODES, "CA-ON");
      properties.setProperty(PROPERTY_ZONE + ".121." + ISO3166_CODES, "JP-12");
      properties.setProperty(PROPERTY_ZONE + ".141." + ISO3166_CODES, "SG");
      properties.setProperty(PROPERTY_API_VERSION, IBMSmartCloudAsyncClient.VERSION);
      properties.setProperty(PROPERTY_ENDPOINT, "https://www-147.ibm.com/computecloud/enterprise/api/rest");
      properties.setProperty(PROPERTY_TIMEOUT_NODE_RUNNING, (15 * 60 * 1000) + "");
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 60 * 5 + "");
      return properties;
   }

   public IBMSmartCloudPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
