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
package org.jclouds.cloudloadbalancers.internal;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.cloudloadbalancers.reference.Region.LON;
import static org.jclouds.location.reference.LocationConstants.ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.ISO3166_CODES;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersAsyncClient;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersContextBuilder;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersPropertiesBuilder;
import org.jclouds.cloudloadbalancers.config.CloudLoadBalancersRestClientModule;
import org.jclouds.cloudloadbalancers.features.LoadBalancerAsyncClient;
import org.jclouds.cloudloadbalancers.features.LoadBalancerClient;
import org.jclouds.cloudloadbalancers.reference.Region;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.openstack.filters.AuthenticateRequest;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule.GetAuth;
import org.jclouds.openstack.keystone.v1_1.domain.Auth;
import org.jclouds.openstack.keystone.v1_1.parse.ParseAuthTest;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.Provides;

/**
 * @author Adrian Cole
 */
public abstract class BaseCloudLoadBalancersAsyncClientTest<T> extends RestClientTest<T> {

   protected String provider;

   public BaseCloudLoadBalancersAsyncClientTest() {
      this.provider = "cloudloadbalancers";
   }

   @Override
   protected Module createModule() {
      return new TestCloudLoadBalancersRestClientModule();
   }

   @ConfiguresRestClient
   @RequiresHttp
   protected static class TestCloudLoadBalancersRestClientModule extends CloudLoadBalancersRestClientModule {
      private TestCloudLoadBalancersRestClientModule() {
         super(new AuthenticationServiceModule());
      }

      @Provides
      @Singleton
      GetAuth provideGetAuth() {
         return new GetAuth(null) {
            @Override
            public Auth apply(Credentials in) {
               return new ParseAuthTest().expected();
            }
         };
      }

   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), AuthenticateRequest.class);
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      try {
         processor.setCaller(new ClassMethodArgs(CloudLoadBalancersAsyncClient.class,
               CloudLoadBalancersAsyncClient.class.getMethod("getLoadBalancerClient", String.class),
               new Object[] { Region.LON }));
      } catch (Exception e) {
         Throwables.propagate(e);
      }
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(provider + ".endpoint", "https://auth");
      overrides.setProperty(PROPERTY_REGIONS, LON);
      overrides.setProperty(PROPERTY_REGION + "." + LON + "." + ISO3166_CODES, "GB-SLG");
      overrides.setProperty(PROPERTY_REGION + "." + LON + "." + ENDPOINT,
            String.format("https://lon.loadbalancers.api.rackspacecloud.com/v${%s}", PROPERTY_API_VERSION));
      return overrides;
   }

   /**
    * this is only here as "cloudloadbalancers" is not in rest.properties
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return RestContextFactory.<LoadBalancerClient, LoadBalancerAsyncClient> contextSpec(provider, "https://auth",
            "1.0", "", "", "identity", "credential", LoadBalancerClient.class, LoadBalancerAsyncClient.class,
            (Class)CloudLoadBalancersPropertiesBuilder.class, (Class)CloudLoadBalancersContextBuilder.class,
            ImmutableSet.<Module> of());
   }
}