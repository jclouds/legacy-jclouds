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

package org.jclouds.cloudstack.config;

import java.util.Map;

import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.features.NetworkAsyncClient;
import org.jclouds.cloudstack.features.NetworkClient;
import org.jclouds.cloudstack.features.OfferingAsyncClient;
import org.jclouds.cloudstack.features.OfferingClient;
import org.jclouds.cloudstack.features.TemplateAsyncClient;
import org.jclouds.cloudstack.features.TemplateClient;
import org.jclouds.cloudstack.features.ZoneAsyncClient;
import org.jclouds.cloudstack.features.ZoneClient;
import org.jclouds.cloudstack.handlers.CloudStackErrorHandler;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the cloudstack connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class CloudStackRestClientModule extends RestClientModule<CloudStackClient, CloudStackAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(ZoneClient.class, ZoneAsyncClient.class)//
            .put(TemplateClient.class, TemplateAsyncClient.class)//
            .put(OfferingClient.class, OfferingAsyncClient.class)//
            .put(NetworkClient.class, NetworkAsyncClient.class)//
            .build();

   public CloudStackRestClientModule() {
      super(CloudStackClient.class, CloudStackAsyncClient.class, DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CloudStackErrorHandler.class);
   }

}
