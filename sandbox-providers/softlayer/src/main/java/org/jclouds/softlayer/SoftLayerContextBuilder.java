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

package org.jclouds.softlayer;

import java.util.List;
import java.util.Properties;

import org.jclouds.softlayer.config.SoftLayerRestClientModule;
import org.jclouds.rest.RestContextBuilder;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class SoftLayerContextBuilder extends
         RestContextBuilder<SoftLayerClient, SoftLayerAsyncClient> {

   public SoftLayerContextBuilder(Properties props) {
      super(SoftLayerClient.class, SoftLayerAsyncClient.class, props);
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new SoftLayerRestClientModule());
   }

}
