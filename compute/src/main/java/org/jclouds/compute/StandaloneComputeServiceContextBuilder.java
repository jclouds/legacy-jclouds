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

package org.jclouds.compute;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.util.List;
import java.util.Properties;

import org.jclouds.PropertiesBuilder;
import org.jclouds.compute.config.StandaloneComputeServiceClientModule;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class StandaloneComputeServiceContextBuilder extends
      ComputeServiceContextBuilder<ComputeService, ComputeService> {

   public StandaloneComputeServiceContextBuilder(Properties props) {
      super(ComputeService.class, ComputeService.class, props);
      if (properties.size() == 0)
         properties.putAll(new PropertiesBuilder().build());
      if (!properties.containsKey("jclouds.provider"))
         properties.setProperty("jclouds.provider", "standalone");
      if (!properties.containsKey(PROPERTY_ENDPOINT))
         properties.setProperty(PROPERTY_ENDPOINT, "standalone");
      if (!properties.containsKey(PROPERTY_API_VERSION))
         properties.setProperty(PROPERTY_API_VERSION, "1");
      if (!properties.containsKey(PROPERTY_IDENTITY))
         properties.setProperty(PROPERTY_IDENTITY, System.getProperty("user.name"));
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new StandaloneComputeServiceClientModule<ComputeService>(ComputeService.class));
   }

}