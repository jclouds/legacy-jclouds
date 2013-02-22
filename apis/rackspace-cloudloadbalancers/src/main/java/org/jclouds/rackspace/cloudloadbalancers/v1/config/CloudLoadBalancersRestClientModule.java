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
package org.jclouds.rackspace.cloudloadbalancers.v1.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.CloudLoadBalancersAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.AccessRuleApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.AccessRuleAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ConnectionApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ConnectionAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ContentCachingApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ContentCachingAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ErrorPageApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ErrorPageAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.HealthMonitorApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.HealthMonitorAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.NodeApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.NodeAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ReportApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ReportAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SSLTerminationApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SSLTerminationAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SessionPersistenceApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SessionPersistenceAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.VirtualIPApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.VirtualIPAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.functions.ConvertLB;
import org.jclouds.rackspace.cloudloadbalancers.v1.handlers.ParseCloudLoadBalancersErrorFromHttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Configures the Rackspace Cloud Load Balancers connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudLoadBalancersRestClientModule extends
         RestClientModule<CloudLoadBalancersApi, CloudLoadBalancersAsyncApi> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(LoadBalancerApi.class, LoadBalancerAsyncApi.class)
            .put(NodeApi.class, NodeAsyncApi.class)
            .put(AccessRuleApi.class, AccessRuleAsyncApi.class)
            .put(VirtualIPApi.class, VirtualIPAsyncApi.class)
            .put(ConnectionApi.class, ConnectionAsyncApi.class)
            .put(HealthMonitorApi.class, HealthMonitorAsyncApi.class)
            .put(SessionPersistenceApi.class, SessionPersistenceAsyncApi.class)
            .put(ContentCachingApi.class, ContentCachingAsyncApi.class)
            .put(SSLTerminationApi.class, SSLTerminationAsyncApi.class)
            .put(ErrorPageApi.class, ErrorPageAsyncApi.class)
            .put(ReportApi.class, ReportAsyncApi.class)
            .build();

   public CloudLoadBalancersRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      install(new FactoryModuleBuilder().build(ConvertLB.Factory.class));
      super.configure();
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
