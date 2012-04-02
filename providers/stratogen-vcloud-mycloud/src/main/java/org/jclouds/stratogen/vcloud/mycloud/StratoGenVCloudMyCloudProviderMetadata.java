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
package org.jclouds.stratogen.vcloud.mycloud;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.vcloud.VCloudApiMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for StratoGen VMware hosting
 * 
 * @author Adrian Cole
 */
public class StratoGenVCloudMyCloudProviderMetadata extends BaseProviderMetadata {

   public StratoGenVCloudMyCloudProviderMetadata() {
      this(builder()
            .id("stratogen-vcloud-mycloud")
            .name("StratoGen VMware hosting")
            .api(new VCloudApiMetadata())
            .homepage(URI.create("http://www.stratogen.net"))
            .console(URI.create("https://mycloud.stratogen.net/cloud/org/YOUR_ORG_HERE"))
            .iso3166Codes("GB"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected StratoGenVCloudMyCloudProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public StratoGenVCloudMyCloudProviderMetadata build() {
         return new StratoGenVCloudMyCloudProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}
