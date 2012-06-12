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
package org.jclouds.openstack.swift;

import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.config.SwiftKeystoneRestClientModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule.KeystoneStorageEndpointModule;
import org.jclouds.rest.RestContext;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;
/**
 * Implementation of {@link ApiMetadata} for OpenStack Swift authenticated with KeyStone
 * 
 * @author Adrian Cole
 */
public class SwiftKeystoneApiMetadata extends SwiftApiMetadata {

   /** The serialVersionUID */
   private static final long serialVersionUID = 820062881469203616L;
   
   public static final TypeToken<RestContext<SwiftKeystoneClient, SwiftKeystoneAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<SwiftKeystoneClient, SwiftKeystoneAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   private static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromApiMetadata(this);
   }

   public SwiftKeystoneApiMetadata() {
      this(builder());
   }

   protected SwiftKeystoneApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = SwiftApiMetadata.defaultProperties();
      properties.remove(PROPERTY_REGIONS);
      return properties;
   }

   public static class Builder extends SwiftApiMetadata.Builder {
      protected Builder(){
         super(SwiftKeystoneClient.class, SwiftKeystoneAsyncClient.class);
         id("swift-keystone")
         .context(CONTEXT_TOKEN)
         .defaultModules(ImmutableSet.<Class<? extends Module>>of(KeystoneStorageEndpointModule.class, SwiftKeystoneRestClientModule.class, SwiftBlobStoreContextModule.class));
      }

      @Override
      public SwiftKeystoneApiMetadata build() {
         return new SwiftKeystoneApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}
