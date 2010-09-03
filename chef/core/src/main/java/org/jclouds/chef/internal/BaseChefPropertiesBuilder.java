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

package org.jclouds.chef.internal;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.chef.reference.ChefConstants.CHEF_BOOTSTRAP_DATABAG;

import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.chef.ChefAsyncClient;

/**
 * Builds properties used in Chef Clients
 * 
 * @author Adrian Cole
 */
public abstract class BaseChefPropertiesBuilder extends PropertiesBuilder {
   @Override
   protected Properties defaultProperties() {
      Properties properties = super.defaultProperties();
      properties.setProperty(PROPERTY_SESSION_INTERVAL, "1");
      properties.setProperty(PROPERTY_API_VERSION, ChefAsyncClient.VERSION);
      properties.setProperty(CHEF_BOOTSTRAP_DATABAG, "bootstrap");
      return properties;
   }

   public BaseChefPropertiesBuilder(Properties properties) {
      super(properties);
   }
}
