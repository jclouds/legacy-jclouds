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

package org.jclouds.opscodeplatform.functions;

import java.util.Map;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.chef.config.BaseChefRestClientModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.opscodeplatform.OpscodePlatformAsyncClient;
import org.jclouds.opscodeplatform.OpscodePlatformClient;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the Opscode Platform connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class OpscodePlatformRestClientModule extends
      BaseChefRestClientModule<OpscodePlatformClient, OpscodePlatformAsyncClient> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
         .put(ChefClient.class, ChefAsyncClient.class)//
         .build();

   public OpscodePlatformRestClientModule() {
      super(OpscodePlatformClient.class, OpscodePlatformAsyncClient.class, DELEGATE_MAP);
   }

}