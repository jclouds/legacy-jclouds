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
package org.jclouds.opscodeplatform.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.opscodeplatform.OpscodePlatform;
import org.jclouds.opscodeplatform.OpscodePlatformAsyncClient;
import org.jclouds.opscodeplatform.OpscodePlatformClient;
import org.jclouds.opscodeplatform.reference.OpscodePlatformConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the OpscodePlatform connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class OpscodePlatformContextModule extends AbstractModule {

   public OpscodePlatformContextModule(String providerName) {
   }

   @Override
   protected void configure() {

   }

   @Provides
   @Singleton
   RestContext<OpscodePlatformAsyncClient, OpscodePlatformClient> provideContext(Closer closer,
            OpscodePlatformAsyncClient asyncApi, OpscodePlatformClient syncApi,
            @OpscodePlatform URI endPoint,
            @Named(OpscodePlatformConstants.PROPERTY_OPSCODEPLATFORM_ENDPOINT) String account) {
      return new RestContextImpl<OpscodePlatformAsyncClient, OpscodePlatformClient>(closer,
               asyncApi, syncApi, endPoint, account);
   }

}