/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.config;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.abiquo.domain.DomainWrapper.wrap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.User;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.features.AdminApi;
import org.jclouds.abiquo.features.AdminAsyncApi;
import org.jclouds.abiquo.features.CloudApi;
import org.jclouds.abiquo.features.CloudAsyncApi;
import org.jclouds.abiquo.features.ConfigApi;
import org.jclouds.abiquo.features.ConfigAsyncApi;
import org.jclouds.abiquo.features.EnterpriseApi;
import org.jclouds.abiquo.features.EnterpriseAsyncApi;
import org.jclouds.abiquo.features.EventApi;
import org.jclouds.abiquo.features.EventAsyncApi;
import org.jclouds.abiquo.features.InfrastructureApi;
import org.jclouds.abiquo.features.InfrastructureAsyncApi;
import org.jclouds.abiquo.features.PricingApi;
import org.jclouds.abiquo.features.PricingAsyncApi;
import org.jclouds.abiquo.features.TaskApi;
import org.jclouds.abiquo.features.TaskAsyncApi;
import org.jclouds.abiquo.features.VirtualMachineTemplateApi;
import org.jclouds.abiquo.features.VirtualMachineTemplateAsyncApi;
import org.jclouds.abiquo.handlers.AbiquoErrorHandler;
import org.jclouds.abiquo.rest.internal.AbiquoHttpAsyncClient;
import org.jclouds.abiquo.rest.internal.AbiquoHttpClient;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.collect.Memoized;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;
import org.jclouds.rest.config.BinderUtils;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Configures the Abiquo connection.
 * 
 * @author Ignasi Barrera
 */
@ConfiguresRestClient
public class AbiquoRestClientModule extends RestClientModule<AbiquoApi, AbiquoAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder() //
         .put(InfrastructureApi.class, InfrastructureAsyncApi.class) //
         .put(EnterpriseApi.class, EnterpriseAsyncApi.class) //
         .put(AdminApi.class, AdminAsyncApi.class) //
         .put(ConfigApi.class, ConfigAsyncApi.class) //
         .put(CloudApi.class, CloudAsyncApi.class) //
         .put(VirtualMachineTemplateApi.class, VirtualMachineTemplateAsyncApi.class) //
         .put(TaskApi.class, TaskAsyncApi.class) //
         .put(EventApi.class, EventAsyncApi.class) //
         .put(PricingApi.class, PricingAsyncApi.class) //
         .build();

   public AbiquoRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void bindAsyncClient() {
      super.bindAsyncClient();
      BinderUtils.bindAsyncClient(binder(), AbiquoHttpAsyncClient.class);
   }

   @Override
   protected void bindClient() {
      super.bindClient();
      BinderUtils.bindClient(binder(), AbiquoHttpClient.class, AbiquoHttpAsyncClient.class,
            ImmutableMap.<Class<?>, Class<?>> of(AbiquoHttpClient.class, AbiquoHttpAsyncClient.class));
   }

   @Override
   protected void configure() {
      super.configure();
      bind(Utils.class).to(ExtendedUtils.class);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(AbiquoErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(AbiquoErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(AbiquoErrorHandler.class);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<User> getCurrentUser(final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, final RestContext<AbiquoApi, AbiquoAsyncApi> context) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, new Supplier<User>() {
         @Override
         public User get() {
            return wrap(context, User.class, context.getApi().getAdminApi().getCurrentUser());
         }
      }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Enterprise> getCurrentEnterprise(final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, final @Memoized Supplier<User> currentUser) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            new Supplier<Enterprise>() {
               @Override
               public Enterprise get() {
                  return currentUser.get().getEnterprise();
               }
            }, seconds, TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @Memoized
   public Supplier<Map<Integer, Datacenter>> getAvailableRegionsIndexedById(
         final AtomicReference<AuthorizationException> authException,
         @Named(PROPERTY_SESSION_INTERVAL) final long seconds, @Memoized final Supplier<Enterprise> currentEnterprise) {
      Supplier<Map<Integer, Datacenter>> availableRegionsMapSupplier = Suppliers2.compose(
            new Function<List<Datacenter>, Map<Integer, Datacenter>>() {
               @Override
               public Map<Integer, Datacenter> apply(final List<Datacenter> datacenters) {
                  // Index available regions by id
                  return Maps.uniqueIndex(datacenters, new Function<Datacenter, Integer>() {
                     @Override
                     public Integer apply(final Datacenter input) {
                        return input.getId();
                     }
                  });
               }
            }, new Supplier<List<Datacenter>>() {
               @Override
               public List<Datacenter> get() {
                  // Get the list of regions available for the user's tenant
                  return currentEnterprise.get().listAllowedDatacenters();
               }
            });

      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
            availableRegionsMapSupplier, seconds, TimeUnit.SECONDS);
   }
}
