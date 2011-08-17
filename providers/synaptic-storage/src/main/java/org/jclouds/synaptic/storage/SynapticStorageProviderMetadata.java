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
package org.jclouds.synaptic.storage;

import com.google.common.collect.ImmutableSet;

import java.net.URI;
import java.util.Set;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for AT&T's
 * Synaptic Storage provider.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class SynapticStorageProviderMetadata extends BaseProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "synaptic-storage";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getType() {
      return ProviderMetadata.BLOBSTORE_TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "AT&T Synaptic Storage";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return "Subtenant ID (UID)";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return "Shared Secret";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("https://www.synaptic.att.com/");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://www.synaptic.att.com/clouduser/login.htm");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getApiDocumentation() {
      return URI.create("https://www.synaptic.att.com/clouduser/emc_atmos_api.htm");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("US-VA", "US-TX");
   }

}