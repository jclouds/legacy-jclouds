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

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule.SwiftTemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule.StorageEndpointModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Module;

/**
 * Implementation of {@link ApiMetadata} for OpenStack Swift
 *
 * @author Adrian Cole
 */
public class SwiftApiMetadata extends BaseRestApiMetadata {
   /** The serialVersionUID */
   private static final long serialVersionUID = 6725672099385580694L;

   public static final TypeToken<RestContext<SwiftClient, SwiftAsyncClient>> CONTEXT_TOKEN = new TypeToken<RestContext<SwiftClient, SwiftAsyncClient>>() {
      private static final long serialVersionUID = -5070937833892503232L;
   };

   @Override
   public Builder toBuilder() {
      return (Builder) new Builder(getApi(), getAsyncApi()).fromApiMetadata(this);
   }

   public SwiftApiMetadata() {
      this(new Builder(SwiftClient.class, SwiftAsyncClient.class));
   }

   protected SwiftApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "X-Object-Meta-");
      properties.setProperty(PROPERTY_REGIONS, "DEFAULT");
      return properties;
   }

   public static class Builder extends BaseRestApiMetadata.Builder {
      protected Builder(Class<?> syncClient, Class<?> asyncClient){
         super(syncClient, asyncClient);
         id("swift")
         .name("OpenStack Swift with SwiftAuth")
         .identityName("tenantId:user")
         .credentialName("password")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.0")
         .defaultProperties(SwiftApiMetadata.defaultProperties())
         .view(TypeToken.of(BlobStoreContext.class))
         .context(CONTEXT_TOKEN)
         .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                     .add(StorageEndpointModule.class)
                                     .add(SwiftRestClientModule.class)
                                     .add(SwiftBlobStoreContextModule.class)
                                     .add(SwiftTemporaryUrlExtensionModule.class).build());
      }

      @Override
      public SwiftApiMetadata build() {
         return new SwiftApiMetadata(this);
      }

      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }
   }
}
