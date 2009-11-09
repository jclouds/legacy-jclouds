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
package org.jclouds.vcloud.terremark.config;

import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientFactory;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;

import com.google.inject.Provides;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class TerremarkVCloudRestClientModule extends VCloudRestClientModule {

   @Provides
   @Singleton
   protected TerremarkVCloudClient provideTerremarkVCloudClient(VCloudClient in) {
      return (TerremarkVCloudClient) in;
   }

   @Override
   protected VCloudClient provideVCloudClient(RestClientFactory factory) {
      return factory.create(TerremarkVCloudClient.class);
   }

}
