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
package org.jclouds.fujitsu.fgcp;

import java.net.URI;

import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.providers.ProviderMetadata} for FGCP.
 * 
 * @author Dies Koper
 */
public class FGCPAUProviderMetadata extends FGCPProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   public FGCPAUProviderMetadata() {
      super(builder());
   }

   public FGCPAUProviderMetadata(Builder builder) {
      super(builder);
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("fgcp-au")
               .name("Fujitsu Global Cloud Platform (FGCP) - AU")
               .apiMetadata(new FGCPApiMetadata())
               .homepage(
                     URI.create("http://www.fujitsu.com/global/solutions/cloud/solutions/global-cloud-platform/index.html"))
               .console(URI.create("http://globalcloud.fujitsu.com.au"))
               .defaultProperties(FGCPApiMetadata.defaultProperties())
               .iso3166Codes("AU-NSW")
               .endpoint(
                     "https://api.globalcloud.fujitsu.com.au/ovissapi/endpoint")
               .defaultProperties(FGCPProviderMetadata.defaultProperties());
      }

      @Override
      public FGCPAUProviderMetadata build() {
         return new FGCPAUProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
