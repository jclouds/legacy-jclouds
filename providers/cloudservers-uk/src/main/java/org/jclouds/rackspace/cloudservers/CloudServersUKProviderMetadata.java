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
package org.jclouds.rackspace.cloudservers;

import java.net.URI;

import org.jclouds.cloudservers.CloudServersApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud Servers in UK.
 * 
 * @author Adrian Cole
 */
public class CloudServersUKProviderMetadata extends BaseProviderMetadata {

   public CloudServersUKProviderMetadata() {
      this(builder()
            .id("cloudservers-uk")
            .name("Rackspace Cloud Servers UK")
            .api(new CloudServersApiMetadata())
            .homepage(URI.create("http://www.rackspace.co.uk/cloud-hosting/cloud-products/cloud-servers"))
            .console(URI.create("https://lon.manage.rackspacecloud.com"))
            .linkedServices("cloudloadbalancers-uk", "cloudservers-uk", "cloudfiles-uk")
            .iso3166Codes("GB-SLG"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected CloudServersUKProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public CloudServersUKProviderMetadata build() {
         return new CloudServersUKProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}