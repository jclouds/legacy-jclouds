/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.snia.cdmi.v1.config;

import java.util.Map;

import javax.inject.Singleton;
import javax.inject.Named;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.snia.cdmi.v1.CDMIApi;
import org.jclouds.snia.cdmi.v1.CDMIAsyncApi;
import org.jclouds.snia.cdmi.v1.features.ContainerApi;
import org.jclouds.snia.cdmi.v1.features.ContainerAsyncApi;
import org.jclouds.snia.cdmi.v1.features.DataApi;
import org.jclouds.snia.cdmi.v1.features.DataAsyncApi;
import org.jclouds.snia.cdmi.v1.features.DataNonCDMIContentTypeApi;
import org.jclouds.snia.cdmi.v1.features.DataNonCDMIContentTypeAsyncApi;
import org.jclouds.snia.cdmi.v1.features.DomainApi;
import org.jclouds.snia.cdmi.v1.features.DomainAsyncApi;
import org.jclouds.snia.cdmi.v1.filters.AuthenticationFilter;
import org.jclouds.snia.cdmi.v1.handlers.CDMIErrorHandler;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Configures the CDMI connection.
 * 
 * @author Kenneth Nagin
 */

@ConfiguresRestClient
public class CDMIRestClientModule extends RestClientModule<CDMIApi, CDMIAsyncApi> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(DomainApi.class, DomainAsyncApi.class).put(ContainerApi.class, ContainerAsyncApi.class)
            .put(DataApi.class, DataAsyncApi.class)
            .put(DataNonCDMIContentTypeApi.class, DataNonCDMIContentTypeAsyncApi.class).build();

   public CDMIRestClientModule() {
      super(DELEGATE_MAP);
   }
   
   public void configure() {
	   super.configure();
   }
   
   @Provides
   @Singleton
   public AuthenticationFilter provideAuthenticationFilterSwitch(Injector i, @Named(CDMIProperties.AUTHTYPE) String authType) {
      return  new AuthenticationFilter(i.getInstance(AuthType.valueOf(authType).getFilterClass()));
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CDMIErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CDMIErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CDMIErrorHandler.class);
   }
}
