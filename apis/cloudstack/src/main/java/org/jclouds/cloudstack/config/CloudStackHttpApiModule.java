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
package org.jclouds.cloudstack.config;

import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.CloudStackDomainApi;
import org.jclouds.cloudstack.CloudStackGlobalApi;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.SessionApi;
import org.jclouds.cloudstack.filters.AddSessionKeyAndJSessionIdToRequest;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.handlers.CloudStackErrorHandler;
import org.jclouds.cloudstack.handlers.InvalidateSessionAndRetryOn401AndLogoutOnClose;
import org.jclouds.cloudstack.loaders.LoginWithPasswordCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.config.HttpApiModule;
import org.jclouds.rest.internal.ApiContextImpl;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;

/**
 * Configures the cloudstack connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresHttpApi
public class CloudStackHttpApiModule extends HttpApiModule<CloudStackApi> {

   @Override
   protected void configure() {
      bind(new TypeLiteral<ApiContext<CloudStackDomainApi>>() {
      }).to(new TypeLiteral<ApiContextImpl<CloudStackDomainApi>>() {
      });
      bind(new TypeLiteral<ApiContext<CloudStackGlobalApi>>() {
      }).to(new TypeLiteral<ApiContextImpl<CloudStackGlobalApi>>() {
      });
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      // session client is used directly for filters and retry handlers, so let's bind it explicitly
      bindHttpApi(binder(), SessionApi.class);
      bindHttpApi(binder(), CloudStackDomainApi.class);
      bindHttpApi(binder(), CloudStackGlobalApi.class);
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(InvalidateSessionAndRetryOn401AndLogoutOnClose.class);
      
      super.configure();
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(OnlyLocationOrFirstZone.class).in(Scopes.SINGLETON);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CloudStackErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CloudStackErrorHandler.class);
   }

   @Singleton
   static class CredentialTypeFromPropertyOrDefault implements javax.inject.Provider<CredentialType> {
      /**
       * use optional injection to supply a default value for credential type. so that we don't have
       * to set a default property.
       */
      @Inject(optional = true)
      @Named(CloudStackProperties.CREDENTIAL_TYPE)
      String credentialType = CredentialType.API_ACCESS_KEY_CREDENTIALS.toString();

      @Override
      public CredentialType get() {
         return CredentialType.fromValue(credentialType);
      }
   }

   /**
    * we use the type of credentials specified at login to determine which way we want to filter the
    * request. <br/>
    * for ex, if we are getting passwords, we know we will need to login/logout. Otherwise we are
    * signing requests.
    */
   @Provides
   @Singleton
   protected AuthenticationFilter authenticationFilterForCredentialType(CredentialType credentialType,
            AddSessionKeyAndJSessionIdToRequest addSessionKeyAndJSessionIdToRequest, QuerySigner querySigner) {
      switch (credentialType) {
         case PASSWORD_CREDENTIALS:
            return addSessionKeyAndJSessionIdToRequest;
         case API_ACCESS_KEY_CREDENTIALS:
            return querySigner;
         default:
            throw new IllegalArgumentException("credential type not supported: " + credentialType);
      }
   }

   // PROPERTY_SESSION_INTERVAL is default to 60 seconds
   @Provides
   @Singleton
   protected LoadingCache<Credentials, LoginResponse> provideLoginResponseCache(
            LoginWithPasswordCredentials getLoginResponse,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(getLoginResponse);
   }

   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<LoginResponse> provideLoginResponseSupplier(final LoadingCache<Credentials, LoginResponse> cache,
         @Provider final Supplier<Credentials> creds) {
      return new Supplier<LoginResponse>() {
         @Override
         public LoginResponse get() {
            return cache.getUnchecked(creds.get());
         }
      };
   }
}
