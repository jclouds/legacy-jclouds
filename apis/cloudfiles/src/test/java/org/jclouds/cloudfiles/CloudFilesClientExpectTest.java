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
package org.jclouds.cloudfiles;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.cloudfiles.internal.BaseCloudFilesRestClientExpectTest;
import org.jclouds.cloudfiles.reference.CloudFilesHeaders;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "CloudFilesClientExpectTest")
public class CloudFilesClientExpectTest extends BaseCloudFilesRestClientExpectTest {

   @Test
   public void testDeleteContainerReturnsTrueOn200And404() {

      HttpRequest deleteContainer = HttpRequest
               .builder()
               .method("DELETE")
               .endpoint(
                        "https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
               .addHeader("X-Auth-Token", authToken).build();

      HttpResponse containerDeleted = HttpResponse.builder().statusCode(204).message("HTTP/1.1 204 No Content").build();

      CloudFilesClient clientWhenContainerExists = requestsSendResponses(initialAuth, responseWithAuth, deleteContainer,
               containerDeleted);
      assert clientWhenContainerExists.deleteContainerIfEmpty("container");

      HttpResponse containerNotFound = HttpResponse.builder().statusCode(404).message("HTTP/1.1 404 Not Found").build();

      CloudFilesClient clientWhenContainerDoesntExist = requestsSendResponses(initialAuth, responseWithAuth, deleteContainer,
               containerNotFound);
      assert clientWhenContainerDoesntExist.deleteContainerIfEmpty("container");
   }

   @Test
   public void testGetCDNMetadataWhenResponseIs2xxReturnsContainerCDNMetadata() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("HEAD")
            .endpoint("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .addHeader(CloudFilesHeaders.CDN_ENABLED, "True")
            .addHeader(CloudFilesHeaders.CDN_LOG_RETENTION, "True")
            .addHeader(CloudFilesHeaders.CDN_TTL, "259200")
            .addHeader(CloudFilesHeaders.CDN_URI, "http://546406d62bf471d7435d-36c33e76d676c80251b3c13ecb603b67.r19.cf1.rackcdn.com")
            .addHeader(CloudFilesHeaders.CDN_SSL_URI, "https://e9f6fe92d217dc013369-36c33e76d676c80251b3c13ecb603b67.ssl.cf1.rackcdn.com")
            .addHeader(CloudFilesHeaders.CDN_STREAMING_URI, "http://0e79346bc0a2564dcc5e-36c33e76d676c80251b3c13ecb603b67.r19.stream.cf1.rackcdn.com")
            .statusCode(204)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      ContainerCDNMetadata containerCDNMetadata = cdnContainerClient.getCDNMetadata("container");
      assertTrue(containerCDNMetadata.isCDNEnabled());
      assertTrue(containerCDNMetadata.isLogRetention());
      assertEquals(containerCDNMetadata.getTTL(), 259200);
      assertEquals(containerCDNMetadata.getCDNUri().toString(), "http://546406d62bf471d7435d-36c33e76d676c80251b3c13ecb603b67.r19.cf1.rackcdn.com");
      assertEquals(containerCDNMetadata.getCDNSslUri().toString(), "https://e9f6fe92d217dc013369-36c33e76d676c80251b3c13ecb603b67.ssl.cf1.rackcdn.com");
      assertEquals(containerCDNMetadata.getCDNStreamingUri().toString(), "http://0e79346bc0a2564dcc5e-36c33e76d676c80251b3c13ecb603b67.r19.stream.cf1.rackcdn.com");
   }

   @Test
   public void testGetCDNMetadataWhenResponseIs404ReturnsNull() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("HEAD")
            .endpoint("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(404)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      assertNull(cdnContainerClient.getCDNMetadata("container"));
   }

   @Test
   public void testUpdateCDNMetadataWhenResponseIs2xxReturnsURI() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("POST")
            .endpoint("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader(CloudFilesHeaders.CDN_TTL, "259200")
            .addHeader(CloudFilesHeaders.CDN_LOG_RETENTION, "true")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .addHeader(CloudFilesHeaders.CDN_ENABLED, "True")
            .addHeader(CloudFilesHeaders.CDN_LOG_RETENTION, "True")
            .addHeader(CloudFilesHeaders.CDN_TTL, "259200")
            .addHeader(CloudFilesHeaders.CDN_URI, "http://546406d62bf471d7435d-36c33e76d676c80251b3c13ecb603b67.r19.cf1.rackcdn.com")
            .addHeader(CloudFilesHeaders.CDN_SSL_URI, "https://e9f6fe92d217dc013369-36c33e76d676c80251b3c13ecb603b67.ssl.cf1.rackcdn.com")
            .addHeader(CloudFilesHeaders.CDN_STREAMING_URI, "http://0e79346bc0a2564dcc5e-36c33e76d676c80251b3c13ecb603b67.r19.stream.cf1.rackcdn.com")
            .statusCode(204)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      URI cdnURI = cdnContainerClient.updateCDN("container", 259200, true);
      assertEquals(cdnURI.toString(), "http://546406d62bf471d7435d-36c33e76d676c80251b3c13ecb603b67.r19.cf1.rackcdn.com");
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testUpdateCDNMetadataWhenResponseIs404ThrowsException() {
      HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("POST")
            .endpoint("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader(CloudFilesHeaders.CDN_TTL, "259200")
            .addHeader(CloudFilesHeaders.CDN_LOG_RETENTION, "true")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(404)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      cdnContainerClient.updateCDN("container", 259200, true);
   }
   
   @Test
   public void testPurgeCDNObjectWhenResponseIs2xxReturnsTrue() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("DELETE")
            .endpoint("https://cdn3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container/foo.txt")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(204)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      assertTrue(cdnContainerClient.purgeCDNObject("container", "foo.txt"));
   }  

   @Test
   public void testSetCDNStaticWebsiteIndexWhenResponseIs2xxReturnsTrue() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("POST")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader(CloudFilesHeaders.CDN_WEBSITE_INDEX, "index.html")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(204)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      assertTrue(cdnContainerClient.setCDNStaticWebsiteIndex("container", "index.html"));
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testSetCDNStaticWebsiteIndexWhenResponseIs404ThrowsException() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("POST")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader(CloudFilesHeaders.CDN_WEBSITE_INDEX, "index.html")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(404)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      cdnContainerClient.setCDNStaticWebsiteIndex("container", "index.html");
   }

   @Test
   public void testSetCDNStaticWebsiteErrorWhenResponseIs2xxReturnsTrue() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("POST")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader(CloudFilesHeaders.CDN_WEBSITE_ERROR, "error.html")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(204)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      assertTrue(cdnContainerClient.setCDNStaticWebsiteError("container", "error.html"));
   }

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testSetCDNStaticWebsiteErrorWhenResponseIs404ThrowsException() {
	   HttpRequest cdnContainerRequest = HttpRequest.builder()
            .method("POST")
            .endpoint("https://storage101.lon3.clouddrive.com/v1/MossoCloudFS_83a9d536-2e25-4166-bd3b-a503a934f953/container")
            .addHeader(CloudFilesHeaders.CDN_WEBSITE_ERROR, "error.html")
            .addHeader("X-Auth-Token", authToken)
            .build();

      HttpResponse cdnContainerResponse = HttpResponse.builder()
            .statusCode(404)
            .build();

      CloudFilesClient cdnContainerClient = requestsSendResponses(
            initialAuth, responseWithAuth, cdnContainerRequest, cdnContainerResponse);

      cdnContainerClient.setCDNStaticWebsiteError("container", "error.html");
   }
}
