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
package org.jclouds.cloudstack.config;

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.cloudstack.CloudStackAsyncClient;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.CloudStackDomainAsyncClient;
import org.jclouds.cloudstack.CloudStackDomainClient;
import org.jclouds.cloudstack.CloudStackGlobalAsyncClient;
import org.jclouds.cloudstack.CloudStackGlobalClient;
import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.cloudstack.features.*;
import org.jclouds.cloudstack.filters.AddSessionKeyAndJSessionIdToRequest;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.functions.LoginWithPasswordCredentials;
import org.jclouds.cloudstack.handlers.CloudStackErrorHandler;
import org.jclouds.cloudstack.handlers.InvalidateSessionAndRetryOn401AndLogoutOnClose;
import org.jclouds.concurrent.RetryOnTimeOutExceptionFunction;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.implicit.OnlyLocationOrFirstZone;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.BinderUtils;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
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
@ConfiguresRestClient
public class CloudStackRestClientModule extends RestClientModule<CloudStackClient, CloudStackAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(ZoneClient.class, ZoneAsyncClient.class)//
            .put(GlobalZoneClient.class, GlobalZoneAsyncClient.class)//
            .put(TemplateClient.class, TemplateAsyncClient.class)//
            .put(OfferingClient.class, OfferingAsyncClient.class)//
            .put(NetworkClient.class, NetworkAsyncClient.class)//
            .put(VirtualMachineClient.class, VirtualMachineAsyncClient.class)//
            .put(SecurityGroupClient.class, SecurityGroupAsyncClient.class)//
            .put(AsyncJobClient.class, AsyncJobAsyncClient.class)//
            .put(AddressClient.class, AddressAsyncClient.class)//
            .put(NATClient.class, NATAsyncClient.class)//
            .put(FirewallClient.class, FirewallAsyncClient.class)//
            .put(LoadBalancerClient.class, LoadBalancerAsyncClient.class)//
            .put(GuestOSClient.class, GuestOSAsyncClient.class)//
            .put(HypervisorClient.class, HypervisorAsyncClient.class)//
            .put(ConfigurationClient.class, ConfigurationAsyncClient.class)//
            .put(GlobalConfigurationClient.class, GlobalConfigurationAsyncClient.class)//
            .put(AccountClient.class, AccountAsyncClient.class)//
            .put(DomainAccountClient.class, DomainAccountAsyncClient.class)//
            .put(DomainUserClient.class, DomainUserAsyncClient.class)//
            .put(DomainDomainClient.class, DomainDomainAsyncClient.class)//
            .put(GlobalDomainClient.class, GlobalDomainAsyncClient.class)//
            .put(GlobalAccountClient.class, GlobalAccountAsyncClient.class)//
            .put(GlobalUserClient.class, GlobalUserAsyncClient.class)//
            .put(EventClient.class, EventAsyncClient.class)//
            .put(LimitClient.class, LimitAsyncClient.class)//
            .put(DomainLimitClient.class, DomainLimitAsyncClient.class)//
            .put(SSHKeyPairClient.class, SSHKeyPairAsyncClient.class)//
            .put(VMGroupClient.class, VMGroupAsyncClient.class)//
            .put(ISOClient.class, ISOAsyncClient.class)//
            .put(VolumeClient.class, VolumeAsyncClient.class)//
            .put(SnapshotClient.class, SnapshotAsyncClient.class)//
            .put(GlobalAlertClient.class, GlobalAlertAsyncClient.class)//
            .put(GlobalCapacityClient.class, GlobalCapacityAsyncClient.class)//
            .put(GlobalOfferingClient.class, GlobalOfferingAsyncClient.class)//
            .put(GlobalHostClient.class, GlobalHostAsyncClient.class)//
            .put(GlobalStoragePoolClient.class, GlobalStoragePoolAsyncClient.class)//
            .put(GlobalUsageClient.class, GlobalUsageAsyncClient.class)//
            .put(GlobalPodClient.class, GlobalPodAsyncClient.class)//
            .put(GlobalVlanClient.class, GlobalVlanAsyncClient.class)//
            .put(SessionClient.class, SessionAsyncClient.class)//
            .build();

   @Override
   protected void bindAsyncClient() {
      // bind the user client (default)
      super.bindAsyncClient();
      // bind the domain admin client
      BinderUtils.bindAsyncClient(binder(), CloudStackDomainAsyncClient.class);
      // bind the global admin client
      BinderUtils.bindAsyncClient(binder(), CloudStackGlobalAsyncClient.class);
   }

   @Override
   protected void bindClient() {
      // bind the user client (default)
      super.bindClient();
      // bind the domain admin client
      BinderUtils.bindClient(binder(), CloudStackDomainClient.class, CloudStackDomainAsyncClient.class, DELEGATE_MAP);
      // bind the domain admin client
      BinderUtils.bindClient(binder(), CloudStackGlobalClient.class, CloudStackGlobalAsyncClient.class, DELEGATE_MAP);
   }

   public CloudStackRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(CloudStackDateAdapter.class);
      bind(new TypeLiteral<RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudStackDomainClient, CloudStackDomainAsyncClient>>() {
      });
      bind(new TypeLiteral<RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient>>() {
      }).to(new TypeLiteral<RestContextImpl<CloudStackGlobalClient, CloudStackGlobalAsyncClient>>() {
      });
      bind(CredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
      
      // session client is used directly for filters and retry handlers, so let's bind it explicitly
      bindClientAndAsyncClient(binder(), SessionClient.class, SessionAsyncClient.class);
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

   @Provides
   @Singleton
   protected Function<Credentials, LoginResponse> makeSureFilterRetriesOnTimeout(
            LoginWithPasswordCredentials loginWithPasswordCredentials) {
      // we should retry on timeout exception logging in.
      return new RetryOnTimeOutExceptionFunction<Credentials, LoginResponse>(loginWithPasswordCredentials);
   }

   // TODO: not sure we can action the timeout from loginresponse without extra code? modify default
   // accordingly
   // PROPERTY_SESSION_INTERVAL is default to 60 seconds
   @Provides
   @Singleton
   public LoadingCache<Credentials, LoginResponse> provideLoginResponseCache(
            Function<Credentials, LoginResponse> getLoginResponse,
            @Named(Constants.PROPERTY_SESSION_INTERVAL) int seconds) {
      return CacheBuilder.newBuilder().expireAfterWrite(seconds, TimeUnit.SECONDS).build(
               CacheLoader.from(getLoginResponse));
   }

   // Temporary conversion of a cache to a supplier until there is a single-element cache
   // http://code.google.com/p/guava-libraries/issues/detail?id=872
   @Provides
   @Singleton
   protected Supplier<LoginResponse> provideLoginResponseSupplier(final LoadingCache<Credentials, LoginResponse> cache,
            @Provider final Credentials creds) {
      return new Supplier<LoginResponse>() {
         @Override
         public LoginResponse get() {
            try {
               return cache.get(creds);
            } catch (ExecutionException e) {
               throw propagate(e.getCause());
            }
         }
      };
   }
}
