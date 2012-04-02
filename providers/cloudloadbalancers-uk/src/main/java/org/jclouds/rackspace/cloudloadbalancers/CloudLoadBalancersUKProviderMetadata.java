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
package org.jclouds.rackspace.cloudloadbalancers;

import java.net.URI;

import org.jclouds.cloudloadbalancers.CloudLoadBalancersApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud LoadBalancers in UK.
 * 
 * @author Dan Lo Bianco
 */
public class CloudLoadBalancersUKProviderMetadata extends BaseProviderMetadata {

   public CloudLoadBalancersUKProviderMetadata() {
      this(builder()
            .id("cloudloadbalancers-uk")
            .name("Rackspace Cloud Load Balancers UK")
            .api(new CloudLoadBalancersApiMetadata())
            .homepage(URI.create("http://www.rackspace.co.uk/cloud-hosting/cloud-products/cloud-load-balancers"))
            .console(URI.create("https://lon.manage.rackspacecloud.com"))
            .linkedServices("cloudloadbalancers-uk", "cloudservers-uk", "cloudfiles-uk")
            .iso3166Codes("GB-SLG"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected CloudLoadBalancersUKProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public CloudLoadBalancersUKProviderMetadata build() {
         return new CloudLoadBalancersUKProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}