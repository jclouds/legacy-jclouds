/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.terremark.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.io.IOException;
import java.util.Map;
 
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.config.BaseVCloudExpressRestClientModule;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.terremark.TerremarkVCloudAsyncClient;
import org.jclouds.vcloud.terremark.TerremarkVCloudClient;
import org.jclouds.vcloud.terremark.endpoints.KeysList;
import org.jclouds.vcloud.terremark.handlers.ParseTerremarkVCloudErrorFromHttpResponse;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.inject.Provides;

public abstract class TerremarkRestClientModule<S extends TerremarkVCloudClient, A extends TerremarkVCloudAsyncClient>
         extends BaseVCloudExpressRestClientModule<S, A> {

   public TerremarkRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Singleton
   @Provides
   @Named("CreateInternetService")
   String provideCreateInternetService() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateInternetService.xml"));
   }

   @Singleton
   @Provides
   @Named("CreateNodeService")
   String provideCreateNodeService() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/terremark/CreateNodeService.xml"));
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseTerremarkVCloudErrorFromHttpResponse.class);
   }

   @Singleton
   public static class OrgNameToKeysListSupplier implements Supplier<Map<String, ReferenceType>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      private final TerremarkVCloudClient client;

      @Inject
      protected OrgNameToKeysListSupplier(Supplier<VCloudSession> sessionSupplier, TerremarkVCloudClient client) {
         this.sessionSupplier = sessionSupplier;
         this.client = client;
      }

      @Override
      public Map<String, ReferenceType> get() {
         return Maps.transformValues(sessionSupplier.get().getOrgs(), new Function<ReferenceType, ReferenceType>() {

            @Override
            public ReferenceType apply(ReferenceType from) {
               return client.findOrgNamed(from.getName()).getKeysList();
            }

         });
      }
   }

   @Provides
   @Singleton
   @KeysList
   protected Supplier<Map<String, ReferenceType>> provideOrgToKeysListCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgNameToKeysListSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, ReferenceType>>(authException,
               seconds, new Supplier<Map<String, ReferenceType>>() {
                  @Override
                  public Map<String, ReferenceType> get() {
                     return supplier.get();
                  }
               });
   }

   @Singleton
   @Provides
   @Named("CreateKey")
   String provideCreateKey() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/terremark/CreateKey.xml"));
   }
}