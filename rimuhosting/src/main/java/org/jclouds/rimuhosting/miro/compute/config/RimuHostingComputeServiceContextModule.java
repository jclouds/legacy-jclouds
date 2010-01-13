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
package org.jclouds.rimuhosting.miro.compute.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.internal.ComputeServiceContextImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.rimuhosting.miro.RimuHosting;
import org.jclouds.rimuhosting.miro.RimuHostingAsyncClient;
import org.jclouds.rimuhosting.miro.RimuHostingClient;
import org.jclouds.rimuhosting.miro.compute.RimuHostingComputeService;
import org.jclouds.rimuhosting.miro.reference.RimuHostingConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the {@link RimuHostingComputeServiceContext}; requires {@link RimuHostingComputeService} bound.
 * 
 * @author Adrian Cole
 */
public class RimuHostingComputeServiceContextModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(ComputeService.class).to(RimuHostingComputeService.class).asEagerSingleton();
   }

   @Provides
   @Singleton
   ComputeServiceContext<RimuHostingAsyncClient, RimuHostingClient> provideContext(Closer closer,
            ComputeService computeService, RimuHostingAsyncClient asynchApi, RimuHostingClient defaultApi,
            @RimuHosting URI endPoint, @Named(RimuHostingConstants.PROPERTY_RIMUHOSTING_APIKEY) String account) {
      return new ComputeServiceContextImpl<RimuHostingAsyncClient, RimuHostingClient>(closer, computeService,
               asynchApi, defaultApi, endPoint, account);
   }

}
