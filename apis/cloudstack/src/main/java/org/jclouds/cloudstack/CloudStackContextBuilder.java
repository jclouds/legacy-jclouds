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
package org.jclouds.cloudstack;

import java.util.List;
import java.util.Properties;

import org.jclouds.cloudstack.compute.config.CloudStackComputeServiceContextModule;
import org.jclouds.cloudstack.config.CloudStackRestClientModule;
import org.jclouds.cloudstack.internal.CloudStackContextImpl;
import org.jclouds.compute.ComputeServiceContextBuilder;

import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
public class CloudStackContextBuilder extends ComputeServiceContextBuilder<CloudStackClient, CloudStackAsyncClient> {

   public CloudStackContextBuilder(Properties props) {
      super(CloudStackClient.class, CloudStackAsyncClient.class, props);
   }

   @Override
   protected void addContextModule(List<Module> modules) {
      modules.add(new CloudStackComputeServiceContextModule());
   }

   @Override
   protected void addClientModule(List<Module> modules) {
      modules.add(new CloudStackRestClientModule());
   }

   @Override
   public CloudStackContext buildComputeServiceContext() {
      return buildInjector().getInstance(CloudStackContextImpl.class);
   }
}
