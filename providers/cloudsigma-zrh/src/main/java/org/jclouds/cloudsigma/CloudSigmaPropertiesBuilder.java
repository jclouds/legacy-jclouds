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

package org.jclouds.cloudsigma;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.cloudsigma.reference.CloudSigmaConstants.PROPERTY_VNC_PASSWORD;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;

/**
 * Builds properties used in CloudSigma Clients
 * 
 * @author Adrian Cole
 */
public class CloudSigmaPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "1.0");
      properties.setProperty(PROPERTY_VNC_PASSWORD, "IL9vs34d");
      return properties;
   }

   public CloudSigmaPropertiesBuilder(Properties properties) {
      super(properties);
   }

   @Override
   public Properties build() {
      Properties props = super.build();
      checkArgument(props.getProperty(PROPERTY_VNC_PASSWORD).length() <= 8,
            "vnc passwords should be less that 8 characters!");
      return props;
   }
}
