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
package org.jclouds.vcloud.director.v1_5;

import java.util.List;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.vcloud.director.v1_5.config.VCloudDirectorRestClientModule;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudDirectorContextBuilder
      extends
      // ComputeServiceContextBuilder<VCloudDirectorClient,
      // VCloudDirectorAsyncClient, VCloudDirectorContext,
      // VCloudDirectorApiMetadata> {
      RestContextBuilder<VCloudDirectorClient, VCloudDirectorAsyncClient, VCloudDirectorContext, VCloudDirectorApiMetadata> {

   public VCloudDirectorContextBuilder(
         ProviderMetadata<VCloudDirectorClient, VCloudDirectorAsyncClient, VCloudDirectorContext, VCloudDirectorApiMetadata> providerMetadata) {
      super(providerMetadata);
   }

   public VCloudDirectorContextBuilder(VCloudDirectorApiMetadata apiMetadata) {
      super(apiMetadata);
   }

   // TODO
   // @Override
   // protected void addContextModule(List<Module> modules) {
   // modules.add(new VCloudDirectorComputeServiceContextModule());
   // }

   protected void addClientModule(List<Module> modules) {
      modules.add(new VCloudDirectorRestClientModule());
   }

}
