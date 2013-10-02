/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudservers.config;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.jclouds.cloudservers.CloudServersAsyncClient;
import org.jclouds.cloudservers.CloudServersClient;
import org.jclouds.cloudservers.handlers.ParseCloudServersErrorFromHttpResponse;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule;
import org.jclouds.openstack.services.Compute;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudServersRestClientModule extends RestClientModule<CloudServersClient, CloudServersAsyncClient> {

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseCloudServersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseCloudServersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseCloudServersErrorFromHttpResponse.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      install(new AuthenticationServiceModule());
   }

   @Provides
   @Singleton
   @Compute
   protected Supplier<URI> provideCloudServers(RegionIdToURISupplier.Factory factory, @ApiVersion String apiVersion) {
      return getLastValueInMap(factory.createForApiTypeAndVersion("cloudServers", apiVersion));
   }

   // TODO: see if we still need this.
   @Provides
   @Singleton
   @TimeStamp
   protected Supplier<Date> provideCacheBusterDate() {
      return memoizeWithExpiration(new Supplier<Date>() {
         public Date get() {
            return new Date();
         }
      }, 1, TimeUnit.SECONDS);
   }
}
