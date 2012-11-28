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
package org.jclouds.cloudloadbalancers.config;

import java.util.Map;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersAsyncClient;
import org.jclouds.cloudloadbalancers.CloudLoadBalancersClient;
import org.jclouds.cloudloadbalancers.features.LoadBalancerAsyncClient;
import org.jclouds.cloudloadbalancers.features.LoadBalancerClient;
import org.jclouds.cloudloadbalancers.features.NodeAsyncClient;
import org.jclouds.cloudloadbalancers.features.NodeClient;
import org.jclouds.cloudloadbalancers.functions.ConvertLB;
import org.jclouds.cloudloadbalancers.handlers.ParseCloudLoadBalancersErrorFromHttpResponse;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Configures theRackspace Cloud Load Balancers connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudLoadBalancersRestClientModule extends
         RestClientModule<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
            .put(LoadBalancerClient.class, LoadBalancerAsyncClient.class)
            .put(NodeClient.class, NodeAsyncClient.class)
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
