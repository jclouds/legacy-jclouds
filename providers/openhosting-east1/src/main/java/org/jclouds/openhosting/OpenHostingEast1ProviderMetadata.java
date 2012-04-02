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
package org.jclouds.openhosting;

import java.net.URI;

import org.jclouds.elasticstack.ElasticStackApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for ElasticHosts London Peer 1.
 *
 * @author Adrian Cole
 */
public class OpenHostingEast1ProviderMetadata extends BaseProviderMetadata {

   public OpenHostingEast1ProviderMetadata() {
      this(builder()
            .id("openhosting-east1")
            .name("OpenHosting East1")
            .api(new ElasticStackApiMetadata())
            .homepage(URI.create("https://east1.openhosting.com"))
            .console(URI.create("https://east1.openhosting.com/accounts"))
            .iso3166Codes("US-VA"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected OpenHostingEast1ProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public OpenHostingEast1ProviderMetadata build() {
         return new OpenHostingEast1ProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}
