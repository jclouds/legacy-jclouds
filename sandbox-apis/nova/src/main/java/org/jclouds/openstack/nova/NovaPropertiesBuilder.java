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

package org.jclouds.openstack.nova;

import org.jclouds.PropertiesBuilder;

import java.util.Properties;

import static org.jclouds.Constants.PROPERTY_API_VERSION;

/**
 * Builds properties used in Openstack Nova Clients
 * 
 * @author Dmitri Babaev
 */
public class NovaPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_API_VERSION, "1.1");
      return properties;
   }

   public NovaPropertiesBuilder(Properties properties) {
      super(properties);
   }

   public NovaPropertiesBuilder() {
      super();
   }
}
