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
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of @ link org.jclouds.types.ProviderMetadata} for testing.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class JcloudsTestComputeProviderMetadata extends BaseProviderMetadata {
   
   /** The serialVersionUID */
   private static final long serialVersionUID = 424799830416415960L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromProviderMetadata(this));
   }
   
   public JcloudsTestComputeProviderMetadata() {
      super(builder());
   }

   public JcloudsTestComputeProviderMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("test-compute-api")
         .name("Test Compute Provider")
         .endpoint("mem2")
         .homepage(URI.create("http://jclouds.org"))
         .console(URI.create("http://jclouds.org/console"))
         .iso3166Codes(ImmutableSet.of("US-VA", "US-CA"))
         .apiMetadata(new JcloudsTestComputeApiMetadata());
      }

      @Override
      public JcloudsTestComputeProviderMetadata build() {
         return new JcloudsTestComputeProviderMetadata(this);
      }

   }

}
