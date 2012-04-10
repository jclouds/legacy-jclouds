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
package org.jclouds.carrenza.vcloud.director;

import java.net.URI;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorApiMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Carrenza vCloud Director
 * 
 * @author dankov
 */
public class CarrenzaVCloudDirectorProviderMetadata extends BaseProviderMetadata {

   public CarrenzaVCloudDirectorProviderMetadata() {
      this(builder()
            .id("carrenza-vcloud-director")
            .name("Carrenza vCloud Director")
            .api(new VCloudDirectorApiMetadata())
            .homepage(URI.create("http://carrenza.com/"))
            .console(URI.create("https://myvdc.carrenza.net/cloud/org/YOUR_ORG_HERE"))
            .iso3166Codes("GB-LND"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected CarrenzaVCloudDirectorProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public CarrenzaVCloudDirectorProviderMetadata build() {
         return new CarrenzaVCloudDirectorProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}
