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
import org.jclouds.apis.ApiType;
import org.jclouds.loadbalancer.LoadBalancerServiceContext;
import org.jclouds.loadbalancer.internal.BaseLoadBalancerServiceApiMetadata;
import org.jclouds.openstack.OpenStackAuthAsyncClient;

import com.google.common.reflect.TypeToken;

/**
 * Implementation of {@link ApiMetadata} for CloudLoadBalancers 1.0 API
 * 
 * @author Adrian Cole
 */
public class CloudLoadBalancersApiMetadata
      extends
      BaseLoadBalancerServiceApiMetadata<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient, LoadBalancerServiceContext<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient>, CloudLoadBalancersApiMetadata> {

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

   protected static Properties defaultProperties() {
      Properties properties = BaseLoadBalancerServiceApiMetadata.Builder.defaultProperties();
      return properties;
   }

   public static class Builder
         extends
         BaseLoadBalancerServiceApiMetadata.Builder<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient, LoadBalancerServiceContext<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient>, CloudLoadBalancersApiMetadata> {

      protected Builder() {
         id("cloudloadbalancers")
         .name("Rackspace Cloud Load Balancers API")
         .identityName("Username")
         .credentialName("API Key")
         .documentation(URI.create("http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch01.html"))
         .version(OpenStackAuthAsyncClient.VERSION)
         .defaultEndpoint("https://auth.api.rackspacecloud.com")
         .javaApi(CloudLoadBalancersClient.class, CloudLoadBalancersAsyncClient.class)
         .defaultProperties(CloudLoadBalancersApiMetadata.defaultProperties())
         .contextBuilder(TypeToken.of(CloudLoadBalancersContextBuilder.class));
      }

      @Override
      public CloudLoadBalancersApiMetadata build() {
         return new CloudLoadBalancersApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(CloudLoadBalancersApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }

   }

}