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
import org.jclouds.openstack.swift.CommonSwiftClientTest.StorageEndpointModule;
import org.jclouds.openstack.swift.SwiftApiMetadata;
import org.jclouds.openstack.swift.blobstore.config.SwiftBlobStoreContextModule;
import org.jclouds.openstack.swift.blobstore.config.TemporaryUrlExtensionModule.SwiftTemporaryUrlExtensionModule;
import org.jclouds.openstack.swift.config.SwiftRestClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SwiftBlobRequestSigner}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "SwiftBlobSignerExpectTest")
public class SwiftBlobSignerExpectTest extends BaseBlobSignerExpectTest {

   public SwiftBlobSignerExpectTest() {
      identity = "test:tester";
      credential = "testing";
   }

   @Override
   protected HttpRequest getBlob() {
      return HttpRequest.builder().method("GET")
            .endpoint("http://storage/container/name")
            .addHeader("X-Auth-Token", "testtoken").build();
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("http://storage/container/name?temp_url_sig=2abd47f6b1c159fe9a45c873aaade8eeeb36a2e1&temp_url_expires=123456792")
            .build();
   }

   @Override
   protected HttpRequest getBlobWithOptions() {
      return HttpRequest.builder().method("GET")
            .endpoint("http://storage/container/name")
            .addHeader("X-Auth-Token", "testtoken")
            .addHeader("Range", "bytes=0-1").build();
   }

   @Override
   protected HttpRequest putBlob() {
      return HttpRequest.builder().method("PUT")
            .endpoint("http://storage/container/name")
            .addHeader("ETag", "00020408")
            .addHeader("Expect", "100-continue")
            .addHeader("X-Auth-Token", "testtoken").build();
   }

   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .endpoint("http://storage/container/name?temp_url_sig=e894c60fa1284cc575cf22d7786bab07b8c33610&temp_url_expires=123456792")
            .addHeader("Expect", "100-continue")
            .build();
   }

   @Override
   protected HttpRequest removeBlob() {
      return HttpRequest.builder().method("DELETE")
            .endpoint("http://storage/container/name")
            .addHeader("X-Auth-Token", "testtoken").build();
   }

   @Override
   protected Map<HttpRequest, HttpResponse> init() {
      HttpRequest authRequest = HttpRequest.builder().method("GET")
            .endpoint("http://auth/v1.0")
            .addHeader("X-Auth-User", identity)
            .addHeader("X-Auth-Key", credential)
            .addHeader("Accept", "*/*")
            .addHeader("Host", "myhost:8080").build();

      HttpResponse authResponse = HttpResponse.builder().statusCode(200)
            .message("HTTP/1.1 200 OK")
            .addHeader("X-Storage-Url", "http://storage")
            .addHeader("X-Auth-Token", "testtoken").build();

      HttpRequest temporaryKeyRequest = HttpRequest.builder().method("HEAD")
            .endpoint("http://storage/")
            .addHeader("Accept", MediaType.WILDCARD)
            .addHeader("X-Auth-Token", "testtoken").build();

      HttpResponse temporaryKeyResponse = HttpResponse.builder().statusCode(200)
            .addHeader(ACCOUNT_TEMPORARY_URL_KEY, "TEMPORARY_KEY").build();

      return ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(authRequest, authResponse)
            .put(temporaryKeyRequest, temporaryKeyResponse).build();
   }

   public static class StaticTimeAndTemporaryUrlKeyModule extends SwiftTemporaryUrlExtensionModule {

      @Override
      protected Long unixEpochTimestampProvider() {
         return 123456789L;
      }
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new SwiftApiMetadata()
            .toBuilder()
            .defaultEndpoint("http://auth")
            .defaultModules(
                  ImmutableSet.<Class<? extends Module>> builder()
                        .add(StorageEndpointModule.class)
                        .add(SwiftRestClientModule.class)
                        .add(SwiftBlobStoreContextModule.class)
                        .add(StaticTimeAndTemporaryUrlKeyModule.class).build()).build();
   }
}
