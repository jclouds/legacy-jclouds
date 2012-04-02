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
package org.jclouds.go2cloud;

import java.net.URI;

import org.jclouds.elasticstack.ElasticStackApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Go2Cloud's
 * Johannesburg1 provider.
 *
 * @author Adrian Cole
 */
public class Go2CloudJohannesburg1ProviderMetadata extends BaseProviderMetadata {

   public Go2CloudJohannesburg1ProviderMetadata() {
      this(builder()
            .id("go2cloud-jhb1")
            .name("Go2Cloud Johannesburg1")
            .api(new ElasticStackApiMetadata())
            .homepage(URI.create("https://jhb1.go2cloud.co.za"))
            .console(URI.create("https://jhb1.go2cloud.co.za/accounts"))
            .iso3166Codes("ZA-GP"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected Go2CloudJohannesburg1ProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public Go2CloudJohannesburg1ProviderMetadata build() {
         return new Go2CloudJohannesburg1ProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}
