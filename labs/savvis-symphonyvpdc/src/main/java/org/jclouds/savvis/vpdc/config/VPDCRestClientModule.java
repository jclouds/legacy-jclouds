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
package org.jclouds.savvis.vpdc.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;
import static org.jclouds.savvis.vpdc.reference.VPDCConstants.PROPERTY_VPDC_TIMEOUT_TASK_COMPLETED;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.Json;
import org.jclouds.location.Provider;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.savvis.vpdc.VPDCAsyncClient;
import org.jclouds.savvis.vpdc.VPDCClient;
import org.jclouds.savvis.vpdc.domain.internal.VCloudSession;
import org.jclouds.savvis.vpdc.features.BrowsingAsyncClient;
import org.jclouds.savvis.vpdc.features.BrowsingClient;
import org.jclouds.savvis.vpdc.features.FirewallAsyncClient;
import org.jclouds.savvis.vpdc.features.FirewallClient;
import org.jclouds.savvis.vpdc.features.ServiceManagementAsyncClient;
import org.jclouds.savvis.vpdc.features.ServiceManagementClient;
import org.jclouds.savvis.vpdc.features.VMAsyncClient;
import org.jclouds.savvis.vpdc.features.VMClient;
import org.jclouds.savvis.vpdc.handlers.VPDCErrorHandler;
import org.jclouds.savvis.vpdc.internal.LoginAsyncClient;
import org.jclouds.savvis.vpdc.internal.LoginClient;
import org.jclouds.savvis.vpdc.internal.VCloudToken;
import org.jclouds.savvis.vpdc.location.FirstNetwork;
import org.jclouds.savvis.vpdc.predicates.TaskSuccess;
import org.jclouds.util.Strings2;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ConfiguresRestClient
public class VPDCRestClientModule extends RestClientModule<VPDCClient, VPDCAsyncClient> {
   @Override
   protected void configure() {
      super.configure();
      bindClientAndAsyncClient(binder(), LoginClient.class, LoginAsyncClient.class);
   }

   @VCloudToken
   @Provides
   @Singleton
   protected Supplier<String> provideVCloudToken(Supplier<VCloudSession> cache) {
      return Suppliers2.compose(new Function<VCloudSession, String>() {

         @Override
         public String apply(VCloudSession input) {
            return checkNotNull(input.getVCloudToken(), "No token present in session");
         }

      }, cache);
   }

   @Provides
   @org.jclouds.savvis.vpdc.internal.Org
   @Singleton
   protected Supplier<Set<org.jclouds.savvis.vpdc.domain.Resource>> provideOrgs(Supplier<VCloudSession> cache,
            @Identity final String user) {
      return Suppliers2.compose(new Function<VCloudSession, Set<org.jclouds.savvis.vpdc.domain.Resource>>() {

         @Override
         public Set<org.jclouds.savvis.vpdc.domain.Resource> apply(VCloudSession input) {
            checkState(input.getOrgs().size() > 0, "No orgs present for user: " + user);
            return input.getOrgs();
         }

      }, cache);
   }

   @Provides
   @org.jclouds.savvis.vpdc.internal.Org
   @Singleton
   protected Supplier<String> provideDefaultOrgId(
            @org.jclouds.savvis.vpdc.internal.Org Supplier<Set<org.jclouds.savvis.vpdc.domain.Resource>> orgs) {
      return Suppliers2.compose(new Function<Set<org.jclouds.savvis.vpdc.domain.Resource>, String>() {

         @Override
         public String apply(Set<org.jclouds.savvis.vpdc.domain.Resource> input) {
            return Iterables.get(input, 0).getId();
         }

      }, orgs);
   }

   @Provides
   @Singleton
   protected Predicate<String> successTester(Injector injector,
            @Named(PROPERTY_VPDC_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<String>(injector.getInstance(TaskSuccess.class), completed);
   }

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(BrowsingClient.class, BrowsingAsyncClient.class)//
            .put(VMClient.class, VMAsyncClient.class)//
            .put(FirewallClient.class, FirewallAsyncClient.class)//
            .put(ServiceManagementClient.class, ServiceManagementAsyncClient.class)//
            .build();

   public VPDCRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Singleton
   @Provides
   protected Set<CIMOperatingSystem> provideOperatingSystems(Json json, @Provider String providerName)
            throws IOException {
      return json.fromJson(Strings2.toStringAndClose(getClass().getResourceAsStream(
               "/" + providerName + "/predefined_operatingsystems.json")), new TypeLiteral<Set<CIMOperatingSystem>>() {
      }.getType());
   }

   @Provides
   @Singleton
   protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final LoginClient login) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
               new Supplier<VCloudSession>() {

                  @Override
                  public VCloudSession get() {
                     return login.login();
                  }
                  @Override
                  public String toString() {
                     return Objects.toStringHelper(login).add("method", "login").toString();
                  }
               }, seconds, TimeUnit.SECONDS);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(VPDCErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(VPDCErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(VPDCErrorHandler.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(FirstNetwork.class).in(Scopes.SINGLETON);
   }

}
