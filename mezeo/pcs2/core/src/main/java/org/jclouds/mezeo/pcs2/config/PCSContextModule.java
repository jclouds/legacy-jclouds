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
package org.jclouds.mezeo.pcs2.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.mezeo.pcs2.PCS;
import org.jclouds.mezeo.pcs2.PCSAsyncClient;
import org.jclouds.mezeo.pcs2.PCSClient;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Configures the PCS connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
public class PCSContextModule extends AbstractModule {
   public PCSContextModule(String providerName) {

   }

   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   RestContext<PCSClient, PCSAsyncClient> provideContext(Closer closer, PCSAsyncClient async,
            PCSClient defaultApi, @PCS URI endPoint,
            @Named(PCSConstants.PROPERTY_PCS2_USER) String account) {
      return new RestContextImpl<PCSClient, PCSAsyncClient>(closer, async, defaultApi, endPoint,
               account);
   }

}