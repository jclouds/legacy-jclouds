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
package org.jclouds.opscodeplatform.config;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.config.BaseChefRestClientModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.opscodeplatform.OpscodePlatform;
import org.jclouds.opscodeplatform.OpscodePlatformAsyncClient;
import org.jclouds.opscodeplatform.OpscodePlatformClient;
import org.jclouds.opscodeplatform.reference.OpscodePlatformConstants;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;

/**
 * Configures the Opscode Platform connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class OpscodePlatformRestClientModule extends
         BaseChefRestClientModule<OpscodePlatformAsyncClient, OpscodePlatformClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap
            .<Class<?>, Class<?>> builder()//
            .put(ChefClient.class, ChefAsyncClient.class)//
            .build();

   public OpscodePlatformRestClientModule() {
      super(OpscodePlatformClient.class, OpscodePlatformAsyncClient.class, DELEGATE_MAP);
   }

   @Provides
   @Singleton
   @OpscodePlatform
   protected URI provideURI(
            @Named(OpscodePlatformConstants.PROPERTY_OPSCODEPLATFORM_ENDPOINT) String endpoint) {
      return URI.create(endpoint);
   }
}