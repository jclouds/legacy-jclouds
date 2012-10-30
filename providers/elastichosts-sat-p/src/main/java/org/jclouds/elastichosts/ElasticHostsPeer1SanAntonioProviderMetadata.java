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
package org.jclouds.elastichosts;

import java.net.URI;
import java.util.Properties;

import org.jclouds.elasticstack.ElasticStackApiMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for ElasticHosts San Antonio Peer 1.
 * 
 * @author Adrian Cole
 */
public class ElasticHostsPeer1SanAntonioProviderMetadata extends BaseProviderMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = -8914180153534735692L;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public ElasticHostsPeer1SanAntonioProviderMetadata() {
      super(builder());
   }

   public ElasticHostsPeer1SanAntonioProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = new Properties();
      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("elastichosts-sat-p")
         .name("ElasticHosts San Antonio Peer 1")
         .apiMetadata(new ElasticStackApiMetadata().toBuilder().version("2.0").build())
         .homepage(URI.create("https://sat-p.elastichosts.com"))
         .console(URI.create("https://sat-p.elastichosts.com/accounts"))
         .iso3166Codes("US-TX")
         .endpoint("https://api-sat-p.elastichosts.com")
         .defaultProperties(ElasticHostsPeer1SanAntonioProviderMetadata.defaultProperties());
      }

      @Override
      public ElasticHostsPeer1SanAntonioProviderMetadata build() {
         return new ElasticHostsPeer1SanAntonioProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }

   }
}
