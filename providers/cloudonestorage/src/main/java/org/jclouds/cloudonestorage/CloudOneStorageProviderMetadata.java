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
package org.jclouds.cloudonestorage;

import java.net.URI;

import org.jclouds.atmos.AtmosApiMetadata;
import org.jclouds.providers.BaseProviderMetadata;

/**
 * Implementation of {@ link org.jclouds.types.ProviderMetadata} for PEER1's
 * CloudOne Storage provider.
 *
 * @author Jeremy Whitlock <jwhitlock@apache.org>
 */
public class CloudOneStorageProviderMetadata extends BaseProviderMetadata {

   public CloudOneStorageProviderMetadata() {
      this(builder()
            .id("cloudonestorage")
            .name("PEER1 CloudOne Storage")
            .api(new AtmosApiMetadata())
            .homepage(URI.create("http://www.peer1.com/hosting/cloudone-storage.php"))
            .console(URI.create("https://mypeer1.com/"))
            .iso3166Codes("US-GA", "US-TX"));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected CloudOneStorageProviderMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public CloudOneStorageProviderMetadata build() {
         return new CloudOneStorageProviderMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   public ConcreteBuilder toBuilder() {
      return builder().fromProviderMetadata(this);
   }
}
