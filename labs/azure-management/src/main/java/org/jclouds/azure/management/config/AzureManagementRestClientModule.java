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
package org.jclouds.azure.management.config;

import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.jclouds.azure.management.AzureManagementApi;
import org.jclouds.azure.management.AzureManagementAsyncApi;
import org.jclouds.azure.management.features.DiskApi;
import org.jclouds.azure.management.features.DiskAsyncApi;
import org.jclouds.azure.management.features.HostedServiceApi;
import org.jclouds.azure.management.features.HostedServiceAsyncApi;
import org.jclouds.azure.management.features.LocationApi;
import org.jclouds.azure.management.features.LocationAsyncApi;
import org.jclouds.azure.management.features.OSImageApi;
import org.jclouds.azure.management.features.OSImageAsyncApi;
import org.jclouds.azure.management.features.OperationApi;
import org.jclouds.azure.management.features.OperationAsyncApi;
import org.jclouds.azure.management.features.RoleApi;
import org.jclouds.azure.management.features.RoleAsyncApi;
import org.jclouds.azure.management.suppliers.KeyStoreSupplier;
import org.jclouds.azure.management.suppliers.SSLContextWithKeysSupplier;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.TypeLiteral;

/**
 * Configures the Azure Service Management connection.
 * 
 * @author Gerald Pereira
 */
@ConfiguresRestClient
public class AzureManagementRestClientModule extends RestClientModule<AzureManagementApi, AzureManagementAsyncApi> {
   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(LocationApi.class, LocationAsyncApi.class)
         .put(RoleApi.class, RoleAsyncApi.class)
         .put(HostedServiceApi.class, HostedServiceAsyncApi.class)
         .put(OSImageApi.class, OSImageAsyncApi.class)
         .put(OperationApi.class, OperationAsyncApi.class)
         .put(DiskApi.class, DiskAsyncApi.class).build();

   public AzureManagementRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Supplier<SSLContext>>() {
      }).to(new TypeLiteral<SSLContextWithKeysSupplier>() {
      });
      bind(new TypeLiteral<Supplier<KeyStore>>() {
      }).to(new TypeLiteral<KeyStoreSupplier>() {
      });
   }

}
