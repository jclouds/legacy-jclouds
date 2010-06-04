/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.ibmdev.IBMDeveloperCloud;
import org.jclouds.ibmdev.IBMDeveloperCloudAsyncClient;
import org.jclouds.ibmdev.IBMDeveloperCloudClient;
import org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the IBMDeveloperCloud connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class IBMDeveloperCloudContextModule extends AbstractModule {

   public IBMDeveloperCloudContextModule(String providerName) {
      // providerName ignored right now
   }

   @Override
   protected void configure() {
      // example of how to customize bindings
      // bind(DateAdapter.class).to(CDateAdapter.class);
   }

   @Provides
   @Singleton
   RestContext<IBMDeveloperCloudAsyncClient, IBMDeveloperCloudClient> provideContext(Closer closer,
            IBMDeveloperCloudAsyncClient asyncApi, IBMDeveloperCloudClient syncApi,
            @IBMDeveloperCloud URI endPoint,
            @Named(IBMDeveloperCloudConstants.PROPERTY_IBMDEVELOPERCLOUD_USER) String account) {
      return new RestContextImpl<IBMDeveloperCloudAsyncClient, IBMDeveloperCloudClient>(closer,
               asyncApi, syncApi, endPoint, account);
   }

}