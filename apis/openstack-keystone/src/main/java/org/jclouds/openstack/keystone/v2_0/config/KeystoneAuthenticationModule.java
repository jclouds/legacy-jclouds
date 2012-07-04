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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.RetryOnTimeOutExceptionFunction;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.location.suppliers.RegionIdsSupplier;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.location.suppliers.ZoneIdsSupplier;
import org.jclouds.location.suppliers.all.RegionToProvider;
import org.jclouds.location.suppliers.all.ZoneToProvider;
import org.jclouds.location.suppliers.derived.RegionIdsFromRegionIdToURIKeySet;
import org.jclouds.location.suppliers.derived.ZoneIdsFromZoneIdToURIKeySet;
import org.jclouds.location.suppliers.implicit.FirstRegion;
import org.jclouds.location.suppliers.implicit.FirstZone;
import org.jclouds.openstack.keystone.v2_0.AuthenticationAsyncClient;
import org.jclouds.openstack.keystone.v2_0.AuthenticationClient;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.functions.AuthenticateApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.v2_0.functions.AuthenticatePasswordCredentials;
import org.jclouds.openstack.keystone.v2_0.handlers.RetryOnRenew;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURIFromAccessForTypeAndVersion;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURISupplier;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToURIFromAccessForTypeAndVersion;
import org.jclouds.openstack.keystone.v2_0.suppliers.ZoneIdToURIFromAccessForTypeAndVersion;
import org.jclouds.rest.annotations.ApiVersion;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * 
 * @author Adrian Cole
 */
public class KeystoneAuthenticationModule extends AbstractModule {
   
   public static class RegionModule extends AbstractModule {
      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().implement(RegionIdToURISupplier.class,
                  RegionIdToURIFromAccessForTypeAndVersion.class).build(RegionIdToURISupplier.Factory.class));
         install(new FactoryModuleBuilder().implement(RegionIdToAdminURISupplier.class,
                  RegionIdToAdminURIFromAccessForTypeAndVersion.class).build(RegionIdToAdminURISupplier.Factory.class));
         // dynamically build the region list as opposed to from properties
         bind(RegionIdsSupplier.class).to(RegionIdsFromRegionIdToURIKeySet.class);
         bind(ImplicitLocationSupplier.class).to(FirstRegion.class).in(Scopes.SINGLETON);
         bind(LocationsSupplier.class).to(RegionToProvider.class).in(Scopes.SINGLETON);
      }

      // supply the region to id map from keystone, based on the servicetype and api version in
      // config
      @Provides
      @Singleton
      protected RegionIdToURISupplier provideRegionIdToURISupplierForApiVersion(
               @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
               RegionIdToURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, apiVersion);
      }

      // supply the region to id to AdminURL map from keystone, based on the servicetype and api
      // version in
      // config
      @Provides
      @Singleton
      protected RegionIdToAdminURISupplier provideRegionIdToAdminURISupplierForApiVersion(
               @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
               RegionIdToAdminURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, apiVersion);
      }

   }

   public static class ZoneModule extends AbstractModule {
      @Override
      protected void configure() {
         install(new FactoryModuleBuilder().implement(ZoneIdToURISupplier.class,
                  ZoneIdToURIFromAccessForTypeAndVersion.class).build(ZoneIdToURISupplier.Factory.class));
         // dynamically build the zone list as opposed to from properties
         bind(ZoneIdsSupplier.class).to(ZoneIdsFromZoneIdToURIKeySet.class);
         bind(ImplicitLocationSupplier.class).to(FirstZone.class).in(Scopes.SINGLETON);
         bind(LocationsSupplier.class).to(ZoneToProvider.class).in(Scopes.SINGLETON);
      }

      // supply the zone to id map from keystone, based on the servicetype and api version in
      // config
      @Provides
      @Singleton
      protected ZoneIdToURISupplier provideZoneIdToURISupplierForApiVersion(
               @Named(KeystoneProperties.SERVICE_TYPE) String serviceType, @ApiVersion String apiVersion,
               ZoneIdToURISupplier.Factory factory) {
         return factory.createForApiTypeAndVersion(serviceType, apiVersion);
      }

   }

   @Override
   protected void configure() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(RetryOnRenew.class);
      bindAuthenticationClient();
   }

   protected void bindAuthenticationClient() {
      // AuthenticationClient is used directly for filters and retry handlers, so let's bind it
      // explicitly
      bindClientAndAsyncClient(binder(), AuthenticationClient.class, AuthenticationAsyncClient.class);
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
   protected Map<String, Function<Credentials, Access>> authenticationMethods(Injector i) {
      Builder<Function<Credentials, Access>> fns = ImmutableSet.<Function<Credentials, Access>> builder();
      fns.add(i.getInstance(AuthenticatePasswordCredentials.class));
      fns.add(i.getInstance(AuthenticateApiAccessKeyCredentials.class));
      return CredentialTypes.indexByCredentialType(fns.build());
   }

   @Provides
   @Singleton
   protected Function<Credentials, Access> authenticationMethodForCredentialType(
            @Named(KeystoneProperties.CREDENTIAL_TYPE) String credentialType,
            Map<String, Function<Credentials, Access>> authenticationMethods) {
      checkArgument(authenticationMethods.containsKey(credentialType), "credential type %s not in supported list: %s",
               credentialType, authenticationMethods.keySet());
      // regardless of how we authenticate, we should retry if there is a timeout exception logging
      // in.
      return new RetryOnTimeOutExceptionFunction<Credentials, Access>(authenticationMethods.get(credentialType));
   }

   // TODO: what is the timeout of the session token? modify default accordingly
   // PROPERTY_SESSION_INTERVAL is default to 60 seconds, but we have this here at 23 hours for now.
   @Provides
   @Singleton
   public LoadingCache<Credentials, Access> provideAccessCache(Function<Credentials, Access> getAccess) {
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

}