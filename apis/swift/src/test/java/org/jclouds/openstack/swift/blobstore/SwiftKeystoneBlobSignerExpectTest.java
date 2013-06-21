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
package org.jclouds.openstack.swift.blobstore;

import static org.jclouds.openstack.swift.reference.SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.internal.BaseBlobSignerExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.config.MappedAuthenticationApiModule;
import org.jclouds.openstack.swift.SwiftKeystoneApiMetadata;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule.SwiftKeystoneTemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftKeystoneRestClientModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule.KeystoneStorageEndpointModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SwiftBlobRequestSigner}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SwiftKeystoneBlobSignerExpectTest")
public class SwiftKeystoneBlobSignerExpectTest extends BaseBlobSignerExpectTest {

   @Override
   protected HttpRequest getBlob() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container/name")
            .addHeader("X-Auth-Token", "Auth_4f173437e4b013bee56d1007").build();
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container/name?temp_url_sig=fd9b09acbc3ce71182240503c803dda4902098a9&temp_url_expires=123456792").build();
   }

   @Override
   protected HttpRequest getBlobWithOptions() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container/name")
            .addHeader("X-Auth-Token", "Auth_4f173437e4b013bee56d1007").addHeader("Range", "bytes=0-1").build();
   }

   @Override
   protected HttpRequest putBlob() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container/name")
            .addHeader("ETag", "00020408")
            .addHeader("Expect", "100-continue")
            .addHeader("X-Auth-Token", "Auth_4f173437e4b013bee56d1007").build();
   }

   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container/name?temp_url_sig=72e5f6ebafab2b3da0586198797e58fb7478211e&temp_url_expires=123456792")
            .addHeader("Expect", "100-continue")
            .build();
   }

   @Override
   protected HttpRequest removeBlob() {
      return HttpRequest.builder().method("DELETE")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/container/name")
            .addHeader("X-Auth-Token", "Auth_4f173437e4b013bee56d1007").build();
   }

   /**
    * add the keystone commands
    */
   @Override
   protected Map<HttpRequest, HttpResponse> init() {

      HttpRequest authenticate = HttpRequest
            .builder()
            .method("POST")
            .endpoint("http://localhost:5000/v2.0/tokens")
            .addHeader("Accept", "application/json")
            .payload(
                  payloadFromStringWithContentType(
                        "{\"auth\":{\"passwordCredentials\":{\"username\":\"identity\",\"password\":\"credential\"}}}",
                        "application/json")).build();

      HttpResponse authenticationResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResourceWithContentType("/keystoneAuthResponse.json", "application/json"))
            .build();

      HttpRequest temporaryKeyRequest = HttpRequest
            .builder()
            .method("HEAD")
            .endpoint("https://objects.jclouds.org/v1.0/40806637803162/")
            .addHeader("Accept", MediaType.WILDCARD)
            .addHeader("X-Auth-Token", "Auth_4f173437e4b013bee56d1007").build();

      HttpResponse temporaryKeyResponse = HttpResponse.builder().statusCode(200)
            .addHeader(ACCOUNT_TEMPORARY_URL_KEY, "TEMPORARY_KEY").build();

      return ImmutableMap.<HttpRequest, HttpResponse> builder()
                         .put(authenticate, authenticationResponse)
                         .put(temporaryKeyRequest, temporaryKeyResponse).build();
   }

   public static class StaticTimeAndTemporaryUrlKeyModule extends SwiftKeystoneTemporaryUrlExtensionModule {
      public static final long UNIX_EPOCH_TIMESTAMP = 123456789L;

      @Override
      protected Long unixEpochTimestampProvider() {
         return UNIX_EPOCH_TIMESTAMP;
      }
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new SwiftKeystoneApiMetadata().toBuilder()
                                   .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                                         .add(MappedAuthenticationApiModule.class)
                                         .add(KeystoneStorageEndpointModule.class)
                                         .add(KeystoneAuthenticationModule.RegionModule.class)
                                         .add(SwiftKeystoneRestClientModule.class)
                                         .add(SwiftBlobStoreContextModule.class)
                                         .add(StaticTimeAndTemporaryUrlKeyModule.class).build()).build();
   }
}
