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
package org.jclouds.rackspace.cloudfiles;

import java.net.URI;
import java.util.Set;

import org.jclouds.cloudfiles.CloudFilesProviderMetadata;

import com.google.common.collect.ImmutableSet;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud Files in UK.
 * 
 * @author Adrian Cole
 */
public class CloudFilesUKProviderMetadata extends CloudFilesProviderMetadata {

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return "cloudfiles-uk";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return "Rackspace Cloud Files UK";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getHomepage() {
      return URI.create("http://www.rackspace.co.uk/cloud-hosting/cloud-products/cloud-files");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getConsole() {
      return URI.create("https://lon.manage.rackspacecloud.com");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getLinkedServices() {
      return ImmutableSet.of("cloudfiles-uk", "cloudservers-uk", "cloudloadbalancers-uk");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getIso3166Codes() {
      return ImmutableSet.of("GB-SLG");
   }

}