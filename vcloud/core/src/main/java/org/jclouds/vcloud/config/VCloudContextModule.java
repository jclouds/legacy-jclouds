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
package org.jclouds.vcloud.config;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.reference.VCloudConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public class VCloudContextModule extends AbstractModule {
   @Override
   protected void configure() {
   }

   @Provides
   @Singleton
   RestContext<VCloudClient, VCloudAsyncClient> provideContext(Closer closer,
         HttpClient http, HttpAsyncClient asyncHttp,
         VCloudAsyncClient asynchApi, VCloudClient defaultApi,
         @Org URI endPoint,
         @Named(VCloudConstants.PROPERTY_VCLOUD_USER) String account) {
      return new RestContextImpl<VCloudClient, VCloudAsyncClient>(closer, http,
            asyncHttp, defaultApi, asynchApi, endPoint, account);
   }

}