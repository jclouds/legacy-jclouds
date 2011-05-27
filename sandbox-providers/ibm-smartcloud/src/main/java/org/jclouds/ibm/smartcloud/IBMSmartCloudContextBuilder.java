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

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.ibm.smartcloud.compute.config.IBMSmartCloudComputeServiceContextModule;
import org.jclouds.ibm.smartcloud.config.IBMSmartCloudRestClientModule;

import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class IBMSmartCloudContextBuilder extends
         ComputeServiceContextBuilder<IBMSmartCloudClient, IBMSmartCloudAsyncClient> {

   public IBMSmartCloudContextBuilder(Properties props) {
      super(IBMSmartCloudClient.class, IBMSmartCloudAsyncClient.class, props);
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new IBMSmartCloudRestClientModule());
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new IBMSmartCloudComputeServiceContextModule());
   }

}
