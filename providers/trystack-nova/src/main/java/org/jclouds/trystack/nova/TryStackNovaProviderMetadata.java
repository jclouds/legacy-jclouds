/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not computee this file except in compliance
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
package org.jclouds.trystack.nova;

import java.net.URI;

import org.jclouds.openstack.nova.v1_1.NovaApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;


/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for TryStack Nova
 * 
 * @author Adrian Cole
 */
public class TryStackNovaProviderMetadata extends BaseProviderMetadata {

   public TryStackNovaProviderMetadata() {
      this(builder()
            .id("trystack-nova")
            .name("TryStack.org (Nova)")
            .api(new NovaApiMetadata())
            .homepage(URI.create("https://trystack.org"))
            .console(URI.create("https://trystack.org/dash"))
            .iso3166Codes("US-CA"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected TryStackNovaProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public TryStackNovaProviderMetadata build() {
         return new TryStackNovaProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}
