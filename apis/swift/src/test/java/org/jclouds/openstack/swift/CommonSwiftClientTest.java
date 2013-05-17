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

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.BlobRequestSigner;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.functions.URIFromAuthenticationResponseForService;
import org.jclouds.openstack.internal.TestOpenStackAuthenticationModule;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.openstack.swift.blobstore.SwiftBlobSigner;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindSwiftObjectMetadataToRequest}
 *
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CommonSwiftClientTest")
public abstract class CommonSwiftClientTest extends BaseAsyncClientTest<SwiftAsyncClient> {

   public static final long UNIX_EPOCH_TIMESTAMP = 123456789L;
   public static final String TEMPORARY_URL_KEY = "get-or-set-X-Account-Meta-Temp-Url-Key";

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   protected String provider = "swift";

   public static class StorageEndpointModule extends TestOpenStackAuthenticationModule {
      @Provides
      @Singleton
      @Storage
      protected Supplier<URI> provideStorageUrl(URIFromAuthenticationResponseForService.Factory factory) {
         return factory.create(AuthHeaders.STORAGE_URL);
      }
   }

   public static class StaticTimeAndTemporaryUrlKeyModule extends TemporaryUrlExtensionModule<SwiftAsyncClient> {
      @Override
      protected Long unixEpochTimestampProvider() {
         return UNIX_EPOCH_TIMESTAMP;
      }

      @Override
      protected void configure() {
         bindTemporaryUrlKeyApi();
         bind(new TypeLiteral<Supplier<String>>() {
         }).annotatedWith(TemporaryUrlKey.class).toInstance(Suppliers.ofInstance(TEMPORARY_URL_KEY));
      }

      @Override
      protected void bindRequestSigner() {
         bind(BlobRequestSigner.class).to(new TypeLiteral<SwiftBlobSigner<SwiftAsyncClient>>() {
         });
      }
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new SwiftApiMetadata().toBuilder()
                                   .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                         .add(StorageEndpointModule.class)
                                         .add(SwiftRestClientModule.class)
                                         .add(SwiftBlobStoreContextModule.class)
                                         .add(StaticTimeAndTemporaryUrlKeyModule.class).build()).build();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "US");
      properties.setProperty(PROPERTY_ENDPOINT, "https://auth");
      properties.setProperty(PROPERTY_API_VERSION, "1");
      return properties;
   }

}
