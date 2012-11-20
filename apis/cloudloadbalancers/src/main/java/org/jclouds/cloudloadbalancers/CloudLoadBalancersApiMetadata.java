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
package org.jclouds.cloudloadbalancers;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.cloudloadbalancers.config.CloudLoadBalancersRestClientModule;
import org.jclouds.cloudloadbalancers.loadbalancer.config.CloudLoadBalancersLoadBalancerContextModule;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for CloudLoadBalancers 1.0 API
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersApiMetadata  extends BaseRestApiMetadata {

   public static final TypeToken<RestContext<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient>>() {
   };

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public CloudLoadBalancersApiMetadata() {
      this(new Builder());
   }

   protected CloudLoadBalancersApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {

      protected Builder() {
         super(CloudLoadBalancersClient.class, CloudLoadBalancersAsyncClient.class);
         id("cloudloadbalancers")
         .name("Rackspace Cloud Load Balancers API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch01.html"))
         .version("1.0")
         .defaultEndpoint("https://auth.api.rackspacecloud.com")
         .defaultProperties(CloudLoadBalancersApiMetadata.defaultProperties())
         .view(TypeToken.of(LoadBalancerServiceContext.class))
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(CloudLoadBalancersRestClientModule.class, CloudLoadBalancersLoadBalancerContextModule.class));
      }

      @Override
      public CloudLoadBalancersApiMetadata build() {
         return new CloudLoadBalancersApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}
