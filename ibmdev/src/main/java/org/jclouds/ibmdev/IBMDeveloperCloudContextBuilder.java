/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.ibmdev;

import java.util.List;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.ibmdev.compute.config.IBMDeveloperCloudComputeServiceContextModule;
import org.jclouds.ibmdev.config.IBMDeveloperCloudRestClientModule;

import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public class IBMDeveloperCloudContextBuilder extends
         ComputeServiceContextBuilder<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient> {

   public IBMDeveloperCloudContextBuilder(String providerName, Properties props) {
      super(providerName, IBMDeveloperCloudClient.class, IBMDeveloperCloudAsyncClient.class, props);
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new IBMDeveloperCloudRestClientModule());
   }

   @Override
   protected void addContextModule(String providerName, List<Module> modules) {
      modules.add(new IBMDeveloperCloudComputeServiceContextModule(providerName));
   }

}
