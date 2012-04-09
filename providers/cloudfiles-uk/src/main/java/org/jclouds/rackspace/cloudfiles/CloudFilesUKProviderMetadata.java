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

import org.jclouds.cloudfiles.CloudFilesApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@link org.jclouds.types.ProviderMetadata} for Rackspace Cloud Files in UK.
 * 
 * @author Adrian Cole
 */
public class CloudFilesUKProviderMetadata extends BaseProviderMetadata {

   public CloudFilesUKProviderMetadata() {
      this(builder()
            .id("cloudfiles-uk")
            .name("Rackspace Cloud Files UK")
            .api(new CloudFilesApiMetadata())
            .homepage(URI.create("http://www.rackspace.co.uk/cloud-hosting/cloud-products/cloud-files"))
            .console(URI.create("https://lon.manage.rackspacecloud.com"))
            .linkedServices("cloudfiles-uk", "cloudservers-uk", "cloudloadbalancers-uk")
            .iso3166Codes("GB-SLG"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected CloudFilesUKProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public CloudFilesUKProviderMetadata build() {
         return new CloudFilesUKProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

}