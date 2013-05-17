/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.swift;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;
import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule.SwiftTemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule.StorageEndpointModule;
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

   /**
    * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(SwiftClient.class)} as
    *             {@link SwiftAsyncClient} interface will be removed in jclouds 1.7.
    */
   @Deprecated
   public static final TypeToken<org.jclouds.rest.RestContext<? extends SwiftClient, ? extends SwiftAsyncClient>> CONTEXT_TOKEN = new TypeToken<org.jclouds.rest.RestContext<? extends SwiftClient, ? extends SwiftAsyncClient>>() {
      private static final long serialVersionUID = 1L;
   };

   @Override
   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public SwiftApiMetadata() {
      this(new ConcreteBuilder());
   }

   protected SwiftApiMetadata(Builder<?> builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(PROPERTY_USER_METADATA_PREFIX, "X-Object-Meta-");
      properties.setProperty(PROPERTY_REGIONS, "DEFAULT");
      // Keystone 1.1 expires tokens after 24 hours and allows renewal 1 hour
      // before expiry by default.  We choose a value less than the latter
      // since the former persists between jclouds invocations.
      properties.setProperty(PROPERTY_SESSION_INTERVAL, 30 * 60 + "");
      return properties;
   }

   public abstract static class Builder<T extends Builder<T>> extends BaseRestApiMetadata.Builder<T> {
      @SuppressWarnings("deprecation")
      protected Builder() {
         this(SwiftClient.class, SwiftAsyncClient.class);
      }
      
      protected Builder(Class<?> syncClient, Class<?> asyncClient){
         super(syncClient, asyncClient);
         id("swift")
         .name("OpenStack Swift with SwiftAuth")
         .identityName("tenantId:user")
         .credentialName("password")
         .documentation(URI.create("http://api.openstack.org/"))
         .version("1.0")
         .defaultProperties(SwiftApiMetadata.defaultProperties())
         .view(typeToken(BlobStoreContext.class))
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
   }
   
   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
}
