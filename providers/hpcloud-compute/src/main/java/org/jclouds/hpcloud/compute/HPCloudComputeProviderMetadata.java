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
package org.jclouds.hpcloud.compute;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;


/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for HP Cloud Compute Services.
 * 
 * @author Adrian Cole
 */
public class HPCloudComputeProviderMetadata extends BaseProviderMetadata {

   public HPCloudComputeProviderMetadata() {
      this(builder()
            .id("hpcloud-compute")
            .name("HP Cloud Compute Services")
            .api(new HPCloudComputeApiMetadata())
            .homepage(URI.create("http://hpcloud.com"))
            .console(URI.create("https://manage.hpcloud.com/compute"))
            .linkedServices("hpcloud-compute", "hpcloud-objectstorage")
            .iso3166Codes("US-NV"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected HPCloudComputeProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public HPCloudComputeProviderMetadata build() {
         return new HPCloudComputeProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}
