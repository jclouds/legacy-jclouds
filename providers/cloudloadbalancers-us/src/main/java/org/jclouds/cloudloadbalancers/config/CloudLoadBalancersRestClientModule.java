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
package org.jclouds.cloudloadbalancers.config;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersAsyncClient;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersClient;
import org.jclouds.cloudloadbalancers.features.LoadBalancerAsyncClient;
import org.jclouds.cloudloadbalancers.features.LoadBalancerClient;
import org.jclouds.cloudloadbalancers.handlers.ParseCloudLoadBalancersErrorFromHttpResponse;
import org.jclouds.cloudloadbalancers.reference.RackspaceConstants;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.location.Region;
import org.jclouds.location.config.ProvideRegionToURIViaProperties;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.config.OpenStackAuthenticationModule;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures theRackspace Cloud Load Balancers connection.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class CloudLoadBalancersRestClientModule extends
         RestClientModule<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(LoadBalancerClient.class, LoadBalancerAsyncClient.class)//
            .build();

   public CloudLoadBalancersRestClientModule() {
      super(CloudLoadBalancersClient.class, CloudLoadBalancersAsyncClient.class, DELEGATE_MAP);
   }

   protected void bindRegionsToProvider() {
      bindRegionsToProvider(ProvideRegionToURIViaPropertiesWithAccountID.class);
   }

   @Singleton
   public static class ProvideRegionToURIViaPropertiesWithAccountID extends ProvideRegionToURIViaProperties {

      @Inject
      protected ProvideRegionToURIViaPropertiesWithAccountID(Injector injector,
               @Named("CONSTANTS") Multimap<String, String> constants,
               @Named(RackspaceConstants.PROPERTY_ACCOUNT_ID) String accountID) {
         super(injector, constants);
         constants.replaceValues(RackspaceConstants.PROPERTY_ACCOUNT_ID, ImmutableSet.of(accountID));
      }
   }

   protected void bindRegionsToProvider(Class<? extends javax.inject.Provider<Map<String, URI>>> providerClass) {
      bind(new TypeLiteral<Map<String, URI>>() {
      }).annotatedWith(Region.class).toProvider(providerClass).in(Scopes.SINGLETON);
   }

   @Override
   protected void configure() {
      install(new OpenStackAuthenticationModule());
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      bindRegionsToProvider();
      super.configure();
   }

   @Provides
   @Singleton
   @Named(RackspaceConstants.PROPERTY_ACCOUNT_ID)
   protected String accountID(Supplier<AuthenticationResponse> in) {
      URI serverURL = in.get().getServices().get(AuthHeaders.SERVER_MANAGEMENT_URL);
      return serverURL.getPath().substring(serverURL.getPath().lastIndexOf('/') + 1);
   }

   @Provides
   @Singleton
   @Region
   public Set<String> regions(@Region Map<String, URI> endpoints) {
      return endpoints.keySet();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
               ParseCloudLoadBalancersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
               ParseCloudLoadBalancersErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
               ParseCloudLoadBalancersErrorFromHttpResponse.class);
   }

}
