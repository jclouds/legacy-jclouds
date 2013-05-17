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
package org.jclouds.cloudfiles.config;

import static org.jclouds.reflect.Reflection2.typeToken;

import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.cloudfiles.CDNManagement;
import org.jclouds.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v1_1.config.AuthenticationServiceModule;
import org.jclouds.openstack.keystone.v1_1.suppliers.V1DefaultRegionIdSupplier;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.config.SwiftRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 *
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CloudFilesRestClientModule extends SwiftRestClientModule<CloudFilesClient, CloudFilesAsyncClient> {
   public CloudFilesRestClientModule() {
      super(typeToken(CloudFilesClient.class), typeToken(CloudFilesAsyncClient.class), ImmutableMap
               .<Class<?>, Class<?>> of());
   }

   @Override
   protected void bindResolvedClientsToCommonSwift() {
      bind(CommonSwiftClient.class).to(CloudFilesClient.class).in(Scopes.SINGLETON);
      bind(CommonSwiftAsyncClient.class).to(CloudFilesAsyncClient.class).in(Scopes.SINGLETON);
   }

   public static class StorageAndCDNManagementEndpointModule extends AuthenticationServiceModule {
      @Provides
      @Singleton
      @CDNManagement
      protected Supplier<URI> provideCDNUrl(RegionIdToURISupplier.Factory factory,
               V1DefaultRegionIdSupplier.Factory defaultRegion) {
         return valueForKey(factory.createForApiTypeAndVersion("cloudFilesCDN", null),
                  defaultRegion.createForApiType("cloudFilesCDN"));
      }

      @Provides
      @Singleton
      @Storage
      protected Supplier<URI> provideStorageUrl(RegionIdToURISupplier.Factory factory,
               V1DefaultRegionIdSupplier.Factory defaultRegion) {
         return valueForKey(factory.createForApiTypeAndVersion("cloudFiles", null),
                  defaultRegion.createForApiType("cloudFiles"));
      }

   }
   
   /**
    * Supplies a value that corresponds to a particular key in a map, or null, if not found
    */
   @VisibleForTesting
   static <K, V> Supplier<V> valueForKey(final Supplier<Map<K, Supplier<V>>> input, final Supplier<K> key) {
      return new Supplier<V>() {

         @Override
         public V get() {
            K keyToFind = key.get();
            Supplier<V> value = input.get().get(keyToFind);
            return value != null ? value.get() : null;
         }

         @Override
         public String toString() {
            return "withKey()";
         }
      };
   }

}
