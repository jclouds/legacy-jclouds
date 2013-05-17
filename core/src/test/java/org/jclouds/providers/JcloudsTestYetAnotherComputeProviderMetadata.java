/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
public class JcloudsTestYetAnotherComputeProviderMetadata extends BaseProviderMetadata {
   
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromProviderMetadata(this));
   }
   
   public JcloudsTestYetAnotherComputeProviderMetadata() {
      super(builder());
   }

   public JcloudsTestYetAnotherComputeProviderMetadata(Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder(){
         id("test-yet-another-compute-provider")
         .name("Test Yet Another Compute Provider")
         .endpoint("mem3")
         .homepage(URI.create("http://jclouds.org"))
         .console(URI.create("http://jclouds.org/console"))
         .iso3166Codes(ImmutableSet.of("JP-13"))
         .apiMetadata(new JcloudsTestComputeApiMetadata());
      }

      @Override
      public JcloudsTestYetAnotherComputeProviderMetadata build() {
         return new JcloudsTestYetAnotherComputeProviderMetadata(this);
      }

   }

}
