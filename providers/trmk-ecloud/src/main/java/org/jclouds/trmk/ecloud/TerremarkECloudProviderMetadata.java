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
package org.jclouds.trmk.ecloud;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Terremark's Enterprise Cloud.
 * 
 * @author Adrian Cole
 */
public class TerremarkECloudProviderMetadata extends BaseProviderMetadata {
   public TerremarkECloudProviderMetadata() {
      this(builder()
            .id("trmk-ecloud")
            .name("Terremark Enterprise Cloud v2.8")
            .api(new TerremarkECloudApiMetadata())
            .homepage(URI.create("http://www.terremark.com/services/cloudcomputing/theenterprisecloud.aspx"))
            .console(URI.create("https://icenter.digitalops.net"))
            .iso3166Codes("US-FL", "US-VA", "NL-NH", "BR-SP"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected TerremarkECloudProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public TerremarkECloudProviderMetadata build() {
         return new TerremarkECloudProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}