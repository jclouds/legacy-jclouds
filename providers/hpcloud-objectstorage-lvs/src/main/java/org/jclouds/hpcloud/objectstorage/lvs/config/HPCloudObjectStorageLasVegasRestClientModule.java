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
package org.jclouds.hpcloud.objectstorage.lvs.config;

import static org.jclouds.util.Suppliers2.getLastValueInMap;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.hpcloud.objectstorage.lvs.HPCloudObjectStorageLasVegasAsyncClient;
import org.jclouds.hpcloud.objectstorage.lvs.HPCloudObjectStorageLasVegasClient;
import org.jclouds.hpcloud.services.HPExtensionCDN;
import org.jclouds.hpcloud.services.HPExtentionServiceType;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.location.suppliers.RegionIdToURISupplier;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.config.SwiftObjectModule;
import org.jclouds.openstack.swift.handlers.ParseSwiftErrorFromHttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Supplier;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class HPCloudObjectStorageLasVegasRestClientModule extends
         RestClientModule<HPCloudObjectStorageLasVegasClient, HPCloudObjectStorageLasVegasAsyncClient> {

   private final KeystoneAuthenticationModule authModule;

   public HPCloudObjectStorageLasVegasRestClientModule() {
      this(new KeystoneAuthenticationModule());
   }

   public HPCloudObjectStorageLasVegasRestClientModule(KeystoneAuthenticationModule authModule) {
      super(HPCloudObjectStorageLasVegasClient.class, HPCloudObjectStorageLasVegasAsyncClient.class);
      this.authModule = authModule;
   }

   protected void configure() {
      install(authModule);
      install(new SwiftObjectModule());
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }
   
   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseSwiftErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseSwiftErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseSwiftErrorFromHttpResponse.class);
   }

   @Provides
   @Singleton
   CommonSwiftClient provideCommonSwiftClient(HPCloudObjectStorageLasVegasClient in) {
      return in;
   }

   @Provides
   @Singleton
   CommonSwiftAsyncClient provideCommonSwiftClient(HPCloudObjectStorageLasVegasAsyncClient in) {
      return in;
   }
   
   @Provides
   @Singleton
   @HPExtensionCDN
   protected Supplier<URI> provideCDNUrl(RegionIdToURISupplier.Factory factory, @ApiVersion String apiVersion) {
      return getLastValueInMap(factory.createForApiTypeAndVersion(HPExtentionServiceType.CDN, apiVersion));
   }

   @Provides
   @Singleton
   @Storage
   protected Supplier<URI> provideStorageUrl(RegionIdToURISupplier.Factory factory, @ApiVersion String apiVersion) {
      return getLastValueInMap(factory.createForApiTypeAndVersion(ServiceType.OBJECT_STORE, apiVersion));
   }

}
