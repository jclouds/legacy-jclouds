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
package org.jclouds.providers;

import java.net.URI;

import org.jclouds.apis.JcloudsTestComputeApiMetadata;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for testing.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class JcloudsTestComputeProviderMetadata extends BaseProviderMetadata {

   public JcloudsTestComputeProviderMetadata() {
      this(builder()
            .api(new JcloudsTestComputeApiMetadata())
            .id("test-compute-api")
            .name("Test Compute Provider")
            .homepage(URI.create("http://jclouds.org"))
            .console(URI.create("http://jclouds.org/console"))
            .iso3166Codes(ImmutableSet.of("US-VA", "US-CA")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected JcloudsTestComputeProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public JcloudsTestComputeProviderMetadata build() {
         return new JcloudsTestComputeProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}