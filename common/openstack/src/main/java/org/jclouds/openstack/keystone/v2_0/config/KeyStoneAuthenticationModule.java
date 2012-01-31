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
package org.jclouds.openstack.keystone.v2_0.config;

import static com.google.common.base.Throwables.propagate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.concurrent.RetryOnTimeOutExceptionFunction;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.Provider;
import org.jclouds.openstack.Authentication;
import org.jclouds.openstack.keystone.v2_0.ServiceAsyncClient;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.functions.AuthenticateApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.v2_0.functions.AuthenticatePasswordCredentials;
import org.jclouds.openstack.keystone.v2_0.handlers.RetryOnRenew;
import org.jclouds.rest.AsyncClientFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@RequiresHttp
public class KeyStoneAuthenticationModule extends AbstractModule {

   @Override
   protected void configure() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
   }

   /**
    * borrowing concurrency code to ensure that caching takes place properly
    */
   @Provides
   @Singleton
   @Authentication
   protected Supplier<String> provideAuthenticationTokenCache(final Supplier<Access> supplier)
            throws InterruptedException, ExecutionException, TimeoutException {
      return new Supplier<String>() {
         public String get() {
            return supplier.get().getToken().getId();
         }
      };
   }

   @Provides
   @Singleton
   protected ServiceAsyncClient provideServiceClient(AsyncClientFactory factory) {
      return factory.create(ServiceAsyncClient.class);
   }

   @Singleton
   static class CredentialTypeFromPropertyOrDefault implements javax.inject.Provider<CredentialType> {
      /**
       * use optional injection to supply a default value for credential type. so that we don't have
       * to set a default property.
       */
      @Inject(optional = true)
      @Named(KeystoneProperties.CREDENTIAL_TYPE)
      String credentialType = CredentialType.API_ACCESS_KEY_CREDENTIALS.toString();

      @Override
      public CredentialType get() {
         return CredentialType.fromValue(credentialType);
      }
   }

   @Provides
   @Singleton
   protected Function<Credentials, Access> authenticationMethodForCredentialType(CredentialType credentialType,
            AuthenticatePasswordCredentials authenticatePasswordCredentials,
            AuthenticateApiAccessKeyCredentials authenticateApiAccessKeyCredentials) {
      Function<Credentials, Access> authMethod;
      switch (credentialType) {
         case PASSWORD_CREDENTIALS:
            authMethod = authenticatePasswordCredentials;
            break;
         case API_ACCESS_KEY_CREDENTIALS:
            authMethod = authenticateApiAccessKeyCredentials;
            break;
         default:
            throw new IllegalArgumentException("credential type not supported: " + credentialType);
      }
      // regardless of how we authenticate, we should retry if there is a timeout exception logging
      // in.
      return new RetryOnTimeOutExceptionFunction<Credentials, Access>(authMethod);
   }

   @Provides
   @Provider
   protected Credentials provideAuthenticationCredentials(@Named(Constants.PROPERTY_IDENTITY) String userOrAccessKey,
            @Named(Constants.PROPERTY_CREDENTIAL) String keyOrSecretKey) {
      return new Credentials(userOrAccessKey, keyOrSecretKey);
   }

   // TODO: what is the timeout of the session token? modify default accordingly
   // PROPERTY_SESSION_INTERVAL is default to 60 seconds, but we have this here at 23 hours for now.
   @Provides
   @Singleton
   public LoadingCache<Credentials, Access> provideAccessCache2(Function<Credentials, Access> getAccess) {
      return CacheBuilder.newBuilder().expireAfterWrite(23, TimeUnit.HOURS).build(CacheLoader.from(getAccess));
   }

   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<Access> provideAccessSupplier(final LoadingCache<Credentials, Access> cache,
            @Provider final Credentials creds) {
      return new Supplier<Access>() {
         @Override
         public Access get() {
            try {
               return cache.get(creds);
            } catch (ExecutionException e) {
               throw propagate(e.getCause());
            }
         }
      };
   }

   @Provides
   @Singleton
   protected Access provideAccess(Supplier<Access> supplier) {
      return supplier.get();
   }

}