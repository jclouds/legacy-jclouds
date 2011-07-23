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
package org.jclouds.trmk.vcloud_0_8.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudAsyncClient;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudSession;
import org.jclouds.trmk.vcloud_0_8.endpoints.Keys;
import org.jclouds.trmk.vcloud_0_8.handlers.ParseTerremarkVCloudErrorFromHttpResponse;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.inject.Provides;

public abstract class TerremarkRestClientModule<S extends TerremarkVCloudClient, A extends TerremarkVCloudAsyncClient>
      extends BaseVCloudExpressRestClientModule<S, A> {

   public TerremarkRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   public TerremarkRestClientModule(Class<S> syncClientType, Class<A> asyncClientType,
         Map<Class<?>, Class<?>> delegateMap) {
      super(syncClientType, asyncClientType, delegateMap);
   }

   @Singleton
   @Provides
   @Named("CreateInternetService")
   String provideCreateInternetService() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService.xml"));
   }

   @Singleton
   @Provides
   @Named("CreateNodeService")
   String provideCreateNodeService() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService.xml"));
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseTerremarkVCloudErrorFromHttpResponse.class);
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
               return client.findOrgNamed(from.getName()).getKeys();
            }

         });
      }
   }

   @Provides
   @Singleton
   @Keys
   protected Supplier<Map<String, ReferenceType>> provideOrgToKeysListCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         OrgNameToKeysListSupplier supplier) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, ReferenceType>>(
            authException, seconds, supplier);
   }

   @Singleton
   @Provides
   @Named("CreateKey")
   String provideCreateKey() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateKey.xml"));
   }
}