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

package org.jclouds.slicehost;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in Slicehost Connections
 * 
 * @author Adrian Cole
 */
public class SlicehostPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_ISO3166_CODES, "US-IL,US-TX,US-MO");
      properties.setProperty(PROPERTY_ENDPOINT, "https://api.slicehost.com");
      properties.setProperty(PROPERTY_API_VERSION, "1.4.1.1");
      properties.setProperty("jclouds.ssh.max_retries", "8");
      return properties;
   }

   public SlicehostPropertiesBuilder(Properties properties) {
      super(properties);
   }

}
