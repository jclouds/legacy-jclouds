/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.glesys;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.glesys.reference.GleSYSConstants.PROPERTY_GLESYS_DEFAULT_DC;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONE;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_ZONES;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in GleSYS Clients
 * 
 * @author Adrian Cole
 * @author Adam Lowe
 */
public class GleSYSPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.glesys.com");
      properties.setProperty(PROPERTY_API_VERSION, "1");
      properties.setProperty(PROPERTY_ZONES, "Amsterdam,Falkenberg,New York City,Stockholm");
      properties.setProperty(PROPERTY_ISO3166_CODES, "NL-NH,SE-N,US-NY,SE-AB");
      properties.setProperty(PROPERTY_ZONE + ".Amsterdam." + ISO3166_CODES, "NL-NH");
      properties.setProperty(PROPERTY_ZONE + ".Falkenberg." + ISO3166_CODES, "SE-N");
      properties.setProperty(PROPERTY_ZONE + ".New York City." + ISO3166_CODES, "US-NY");
      properties.setProperty(PROPERTY_ZONE + ".Stockholm." + ISO3166_CODES, "SE-AB");
      properties.setProperty("jclouds.ssh.max-retries", "5");
      properties.setProperty("jclouds.ssh.retry-auth", "true");
      properties.setProperty(PROPERTY_GLESYS_DEFAULT_DC, "Falkenberg");
      return properties;
   }

   public GleSYSPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
