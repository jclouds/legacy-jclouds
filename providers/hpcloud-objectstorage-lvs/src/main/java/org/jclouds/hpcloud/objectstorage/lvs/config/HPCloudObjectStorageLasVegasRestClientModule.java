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

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.hpcloud.objectstorage.lvs.HPCloudObjectStorageLasVegasAsyncClient;
import org.jclouds.hpcloud.objectstorage.lvs.HPCloudObjectStorageLasVegasClient;
import org.jclouds.hpcloud.services.HPExtensionCDN;
import org.jclouds.hpcloud.services.HPExtentionServiceType;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.keystone.v2_0.config.KeyStoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Service;
import org.jclouds.openstack.services.ServiceType;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.Storage;
import org.jclouds.openstack.swift.config.SwiftObjectModule;
import org.jclouds.openstack.swift.handlers.ParseSwiftErrorFromHttpResponse;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class HPCloudObjectStorageLasVegasRestClientModule extends
         RestClientModule<HPCloudObjectStorageLasVegasClient, HPCloudObjectStorageLasVegasAsyncClient> {

   private final KeyStoneAuthenticationModule authModule;

   public HPCloudObjectStorageLasVegasRestClientModule() {
      this(new KeyStoneAuthenticationModule());
   }

   public HPCloudObjectStorageLasVegasRestClientModule(KeyStoneAuthenticationModule authModule) {
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

   @Override
   protected void bindRetryHandlers() {
      bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(BackoffLimitedRetryHandler.class);
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
   @Storage
   protected URI provideStorageUrl(Access response) {
      return Iterables.getOnlyElement(Iterables.find(response.getServiceCatalog(), new Predicate<Service>(){
    
         @Override
         public boolean apply(Service input) {
            return input.getId().equals(ServiceType.OBJECT_STORE);
         }
         
      }).getEndpoints()).getPublicURL();
   }

   
   @Provides
   @Singleton
   @HPExtensionCDN
   protected URI provideCDNUrl(Access response) {
	 /*
	  if (response.getServices().get(AuthHeaders.CDN_MANAGEMENT_URL) == null) {
	     return URI.create(cdnEndpoint + response.getServices().get(AuthHeaders.STORAGE_URL).getPath());
	  }
	  // Placeholder for when the Object Storage service returns the CDN Management URL in the headers 
      return response.getServices().get(AuthHeaders.CDN_MANAGEMENT_URL);
      */
      
   		return Iterables.getOnlyElement(Iterables.find(response.getServiceCatalog(), new Predicate<Service>(){
    
         @Override
         public boolean apply(Service input) {
            return input.getId().equals(HPExtentionServiceType.CDN);
         }
         
      }).getEndpoints()).getPublicURL();
       
   }
}
