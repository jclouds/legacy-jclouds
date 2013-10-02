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

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.swift.internal.BaseSwiftExpectTest;
import org.jclouds.openstack.swift.reference.SwiftHeaders;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "SwiftClientExpectTest")
public class SwiftClientExpectTest extends BaseSwiftExpectTest<SwiftClient> {

   @Test
   public void testContainerExistsWhenResponseIs2xxReturnsTrue() {
      HttpRequest headContainer = HttpRequest.builder()
            .method("HEAD")
            .endpoint(swiftEndpointWithHostReplaced + "/foo")
            .addHeader("Accept", MediaType.WILDCARD)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse headContainerResponse = HttpResponse.builder().statusCode(200).build();

      SwiftClient clientWhenContainerExists = requestsSendResponses(authRequest,
            authResponse, headContainer, headContainerResponse);

      assertTrue(clientWhenContainerExists.containerExists("foo"));
   }

   @Test
   public void testContainerExistsWhenResponseIs404ReturnsFalse() {
      HttpRequest headContainer = HttpRequest.builder()
            .method("HEAD")
            .endpoint(swiftEndpointWithHostReplaced + "/foo")
            .addHeader("Accept", MediaType.WILDCARD)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse headContainerResponse = HttpResponse.builder().statusCode(404).build();

      SwiftClient clientWhenContainerDoesntExist = requestsSendResponses(authRequest,
            authResponse, headContainer, headContainerResponse);

      assertFalse(clientWhenContainerDoesntExist.containerExists("foo"));
   }

   @Test
   public void testSetContainerMetadataWhenResponseIs2xxReturnsTrue() {
      HttpRequest setContainerMetadataRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(swiftEndpointWithHostReplaced + "/foo")
            .addHeader(SwiftHeaders.CONTAINER_METADATA_PREFIX + "key", "value")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse setContainerMetadataResponse = HttpResponse.builder().statusCode(204).build();

      SwiftClient clientSetContainerMetadata = requestsSendResponses(authRequest,
            authResponse, setContainerMetadataRequest, setContainerMetadataResponse);

      assertTrue(clientSetContainerMetadata.setContainerMetadata("foo", ImmutableMap.<String, String> of("key", "value")));
   }
   
   @Test(expectedExceptions = HttpResponseException.class)
   public void testSetContainerMetadataWhenResponseIs400ThrowsException() {
      HttpRequest setContainerMetadataRequest = HttpRequest.builder()
              .method("POST")
              .endpoint(swiftEndpointWithHostReplaced + "/foo")
              .addHeader(SwiftHeaders.CONTAINER_METADATA_PREFIX, "value")
              .addHeader("X-Auth-Token", authToken).build();

        HttpResponse setContainerMetadataResponse = HttpResponse.builder()
              .statusCode(400)
              .message("Metadata name cannot be empty").build();

        SwiftClient clientSetContainerMetadata = requestsSendResponses(authRequest,
              authResponse, setContainerMetadataRequest, setContainerMetadataResponse);

        clientSetContainerMetadata.setContainerMetadata("foo", ImmutableMap.<String, String> of("", "value"));
   }

   @Test
   public void testSetContainerMetadataWhenResponseIs404ReturnsFalse() {
      HttpRequest setContainerMetadataRequest = HttpRequest.builder()
              .method("POST")
              .endpoint(swiftEndpointWithHostReplaced + "/foo")
              .addHeader(SwiftHeaders.CONTAINER_METADATA_PREFIX + "key", "value")
              .addHeader("X-Auth-Token", authToken).build();

        HttpResponse setContainerMetadataResponse = HttpResponse.builder()
              .statusCode(404).build();

        SwiftClient clientSetContainerMetadata = requestsSendResponses(authRequest,
              authResponse, setContainerMetadataRequest, setContainerMetadataResponse);

        assertFalse(clientSetContainerMetadata.setContainerMetadata("foo", ImmutableMap.<String, String> of("key", "value")));
   }

   @Test
   public void testDeleteContainerMetadataWhenResponseIs2xxReturnsTrue() {
      HttpRequest deleteContainerMetadataRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(swiftEndpointWithHostReplaced + "/foo")
            .addHeader(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "bar", "")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse deleteContainerMetadataResponse = HttpResponse.builder().statusCode(204).build();

      SwiftClient clientDeleteContainerMetadata = requestsSendResponses(authRequest,
            authResponse, deleteContainerMetadataRequest, deleteContainerMetadataResponse);

      assertTrue(clientDeleteContainerMetadata.deleteContainerMetadata("foo", ImmutableList.<String> of("bar")));
   }

   @Test
   public void testDeleteContainerMetadataEmptyWhenResponseIs2xxReturnsTrue() {
      HttpRequest deleteContainerMetadataRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(swiftEndpointWithHostReplaced + "/foo")
            .addHeader(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX, "")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse deleteContainerMetadataResponse = HttpResponse.builder().statusCode(204).build();

      SwiftClient clientDeleteContainerMetadata = requestsSendResponses(authRequest,
            authResponse, deleteContainerMetadataRequest, deleteContainerMetadataResponse);

      assertTrue(clientDeleteContainerMetadata.deleteContainerMetadata("foo", ImmutableList.<String> of("")));
   }

   @Test
   public void testDeleteContainerMetadataWhenResponseIs404ReturnsFalse() {
      HttpRequest deleteContainerMetadataRequest = HttpRequest.builder()
            .method("POST")
            .endpoint(swiftEndpointWithHostReplaced + "/foo")
            .addHeader(SwiftHeaders.CONTAINER_DELETE_METADATA_PREFIX + "bar", "")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse deleteContainerMetadataResponse = HttpResponse.builder().statusCode(404).build();

      SwiftClient clientDeleteContainerMetadata = requestsSendResponses(authRequest,
            authResponse, deleteContainerMetadataRequest, deleteContainerMetadataResponse);

      assertFalse(clientDeleteContainerMetadata.deleteContainerMetadata("foo", ImmutableList.<String> of("bar")));
   }

   @Test
   public void testCopyObjectWhenResponseIs2xxReturnsTrue() {
      String sourceContainer = "bar";
      String sourceObject = "foo.txt";
      String sourcePath = "/" + sourceContainer + "/" + sourceObject;
      String destinationContainer = "foo";
      String destinationObject = "bar.txt";
      String destinationPath = "/" + destinationContainer + "/" + destinationObject;
		   
      HttpRequest copyObjectRequest = HttpRequest.builder()
            .method("PUT")
            .endpoint(swiftEndpointWithHostReplaced + destinationPath)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, sourcePath)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse copyObjectResponse = HttpResponse.builder().statusCode(201).build();

      SwiftClient clientCopyObject = requestsSendResponses(authRequest,
            authResponse, copyObjectRequest, copyObjectResponse);

      assertTrue(clientCopyObject.copyObject(sourceContainer, sourceObject, destinationContainer, destinationObject));
   }

   @Test(expectedExceptions = CopyObjectException.class)
   public void testCopyObjectWhenResponseIs404ThrowsException() {
      String sourceContainer = "bar";
      String sourceObject = "foo.txt";
      String sourcePath = "/" + sourceContainer + "/" + sourceObject;
      String destinationContainer = "foo";
      String destinationObject = "bar.txt";
      String destinationPath = "/" + destinationContainer + "/" + destinationObject;
	   
      HttpRequest copyObjectRequest = HttpRequest.builder()
            .method("PUT")
            .endpoint(swiftEndpointWithHostReplaced + destinationPath)
            .addHeader(SwiftHeaders.OBJECT_COPY_FROM, sourcePath)
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse copyObjectResponse = HttpResponse.builder().statusCode(404).build();

      SwiftClient clientCopyObject = requestsSendResponses(authRequest,
            authResponse, copyObjectRequest, copyObjectResponse);

      assertTrue(clientCopyObject.copyObject(sourceContainer, sourceObject, destinationContainer, destinationObject));
   }
}
