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
package org.jclouds.cloudfiles.blobstore;

import static org.jclouds.openstack.swift.reference.SwiftHeaders.ACCOUNT_TEMPORARY_URL_KEY;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.blobstore.internal.BaseBlobSignerExpectTest;
import org.jclouds.cloudfiles.CloudFilesApiMetadata;
import org.jclouds.cloudfiles.CloudFilesApiMetadata.CloudFilesTemporaryUrlExtensionModule;
import org.jclouds.cloudfiles.blobstore.config.CloudFilesBlobStoreContextModule;
import org.jclouds.cloudfiles.config.CloudFilesRestClientModule;
import org.jclouds.cloudfiles.config.CloudFilesRestClientModule.StorageAndCDNManagementEndpointModule;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SwiftBlobSigner}
 *
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CloudFilesBlobSignerExpectTest")
public class CloudFilesBlobSignerExpectTest extends BaseBlobSignerExpectTest {

   public CloudFilesBlobSignerExpectTest() {
      identity = "user@jclouds.org";
      credential = "Password1234";
   }

   @Override
   protected HttpRequest getBlob() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/name")
            .addHeader("X-Auth-Token", authToken).build();
   }

   @Override
   protected HttpRequest getBlobWithTime() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/name?temp_url_sig=9d62a4a15076699b3f7c60c2d021609990f24115&temp_url_expires=123456792").build();
   }

   @Override
   protected HttpRequest getBlobWithOptions() {
      return HttpRequest.builder().method("GET")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/name")
            .addHeader("X-Auth-Token", authToken)
            .addHeader("Range", "bytes=0-1").build();
   }

   @Override
   protected HttpRequest putBlob() {
      return HttpRequest.builder().method("PUT")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/name")
            .addHeader("ETag", "00020408")
            .addHeader("Expect", "100-continue")
            .addHeader("X-Auth-Token", authToken).build();
   }

   @Override
   protected HttpRequest putBlobWithTime() {
      return HttpRequest.builder().method("PUT")
            .addHeader("Expect", "100-continue")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/name?temp_url_sig=f83fa711f353f6f0bab3a66c56e35a972b9b3922&temp_url_expires=123456792").build();
   }

   @Override
   protected HttpRequest removeBlob() {
      return HttpRequest.builder().method("DELETE")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/name")
            .addHeader("X-Auth-Token", authToken).build();
   }

   protected String authToken = "118fb907-0786-4799-88f0-9a5b7963d1ab";

   @Override
   protected Map<HttpRequest, HttpResponse> init() {

      HttpRequest authRequest = HttpRequest.builder().method("POST")
            .endpoint("https://auth.api.rackspacecloud.com/v1.1/auth")
            .addHeader(HttpHeaders.ACCEPT, "application/json")
            .payload(
                  payloadFromStringWithContentType(
                        "{\"credentials\":{\"username\":\"user@jclouds.org\",\"key\":\"Password1234\"}}",
                        "application/json")).build();

      HttpResponse authResponse = HttpResponse.builder().statusCode(200).message("HTTP/1.1 200")
            .payload(payloadFromResourceWithContentType("/auth1_1.json", "application/json")).build();

      HttpRequest temporaryKeyRequest = HttpRequest.builder().method("HEAD")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/")
            .addHeader("Accept", MediaType.WILDCARD)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse temporaryKeyResponse = HttpResponse.builder().statusCode(200)
            .addHeader(ACCOUNT_TEMPORARY_URL_KEY, "TEMPORARY_KEY").build();

      return ImmutableMap.<HttpRequest, HttpResponse> builder()
            .put(authRequest, authResponse)
            .put(temporaryKeyRequest, temporaryKeyResponse).build();
   }

   public static class StaticTimeAndTemporaryUrlKeyModule extends CloudFilesTemporaryUrlExtensionModule {

      @Override
      protected Long unixEpochTimestampProvider() {
         return 123456789L;
      }
   }

   @Override
   protected ApiMetadata createApiMetadata() {
      return new CloudFilesApiMetadata()
            .toBuilder()
            .defaultEndpoint("https://auth.api.rackspacecloud.com")
            .defaultModules(
                  ImmutableSet.<Class<? extends Module>> builder()
                      .add(StorageAndCDNManagementEndpointModule.class)
                      .add(CloudFilesRestClientModule.class)
                      .add(CloudFilesBlobStoreContextModule.class)
                      .add(StaticTimeAndTemporaryUrlKeyModule.class).build()).build();
   }
}
