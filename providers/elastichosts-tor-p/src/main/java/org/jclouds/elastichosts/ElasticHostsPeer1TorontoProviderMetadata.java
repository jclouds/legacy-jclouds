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

import com.google.common.collect.ImmutableSet;
import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import java.net.URI;
import java.util.Set;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for ElasticHosts Toronto Peer 1.
 *
 * @author Adrian Cole
 */
public class ElasticHostsPeer1TorontoProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "elastichosts-tor-p";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.COMPUTE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "ElasticHosts Toronto Peer 1";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "UUID";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "Secret API key";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("https://tor-p.elastichosts.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://tor-p.elastichosts.com/accounts");
   }
   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("http://www.elastichosts.com/cloud-hosting/api");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of("elastichosts-tor-p");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("CA-ON");
   }

}