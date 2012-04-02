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
package org.jclouds.greenhousedata.element.vcloud;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.vcloud.VCloudApiMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Green House Data Element vCloud
 * 
 * @author Adrian Cole
 */
public class GreenHouseDataElementVCloudProviderMetadata extends BaseProviderMetadata {

   public GreenHouseDataElementVCloudProviderMetadata() {
      this(builder()
            .id("greenhousedata-element-vcloud")
            .name("Green House Data Element vCloud")
            .api(new VCloudApiMetadata())
            .homepage(URI.create("http://www.greenhousedata.com/element-cloud-hosting/vcloud-services/"))
            .console(URI.create("https://mycloud.greenhousedata.com/cloud/org/YOUR_ORG_HERE"))
            .iso3166Codes("US-WY"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected GreenHouseDataElementVCloudProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public GreenHouseDataElementVCloudProviderMetadata build() {
         return new GreenHouseDataElementVCloudProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}
