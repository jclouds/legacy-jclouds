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
package org.jclouds.azureblob;

import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.jclouds.azureblob.options.CreateContainerOptions.Builder.withPublicAccess;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import org.jclouds.Fallbacks.TrueOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azureblob.AzureBlobFallbacks.FalseIfContainerAlreadyExists;
import org.jclouds.azureblob.domain.PublicAccess;
import org.jclouds.azureblob.functions.ParseBlobFromHeadersAndHttpContent;
import org.jclouds.azureblob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azureblob.functions.ParsePublicAccessHeader;
import org.jclouds.azureblob.options.CreateContainerOptions;
import org.jclouds.azureblob.options.ListBlobsOptions;
import org.jclouds.azureblob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azureblob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnContainerNotFound;
import org.jclouds.blobstore.BlobStoreFallbacks.NullOnKeyNotFound;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.options.GetOptions;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

/**
 * Tests behavior of {@code AzureBlobAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AzureBlobAsyncClientTest")
public class AzureBlobAsyncClientTest extends BaseAsyncClientTest<AzureBlobAsyncClient> {

   public void testListContainers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", ListOptions[].class);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/?comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);

   }

   public void testListContainersOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", ListOptions[].class);
      HttpRequest request = processor.createRequest(method, maxResults(1).marker("marker").prefix("prefix"));

      assertRequestLineEquals(request,
               "GET https://identity.blob.core.windows.net/?comp=list&maxresults=1&marker=marker&prefix=prefix HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class,
               CreateContainerOptions[].class);
      HttpRequest request = processor.createRequest(method, "container");

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteContainer", String.class);
      HttpRequest request = processor.createRequest(method, "container");

      assertRequestLineEquals(request,
               "DELETE https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class,
               CreateContainerOptions[].class);
      HttpRequest request = processor.createRequest(method, "container", withPublicAccess(PublicAccess.BLOB)
               .withMetadata(ImmutableMultimap.of("foo", "bar")));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "x-ms-blob-public-access: blob\nx-ms-meta-foo: bar\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", CreateContainerOptions[].class);

      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteRootContainer");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "DELETE https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, TrueOnNotFoundOr404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", CreateContainerOptions[].class);
      HttpRequest request = processor.createRequest(method, withPublicAccess(PublicAccess.BLOB).withMetadata(
               ImmutableMultimap.of("foo", "bar")));

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
               "x-ms-blob-public-access: blob\nx-ms-meta-foo: bar\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, FalseIfContainerAlreadyExists.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", String.class, ListBlobsOptions[].class);
      HttpRequest request = processor.createRequest(method, "container");

      assertRequestLineEquals(request,
               "GET https://identity.blob.core.windows.net/container?restype=container&comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);
   }

   public void testListRootBlobs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", ListBlobsOptions[].class);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request,
               "GET https://identity.blob.core.windows.net/$root?restype=container&comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ContainerNameEnumerationResultsHandler.class);
      assertFallbackClassEquals(method, null);
   }

   public void testContainerProperties() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("getContainerProperties", String.class);
      HttpRequest request = processor.createRequest(method, "container");

      assertRequestLineEquals(request,
               "HEAD https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseContainerPropertiesFromHeaders.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnContainerNotFound.class);
   }

   public void testGetPublicAccessForContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("getPublicAccessForContainer", String.class);
      HttpRequest request = processor.createRequest(method, "container");

      assertRequestLineEquals(request,
               "HEAD https://identity.blob.core.windows.net/container?restype=container&comp=acl HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParsePublicAccessHeader.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnContainerNotFound.class);
   }

   public void testSetResourceMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("setResourceMetadata", String.class, Map.class);
      HttpRequest request = processor.createRequest(method,
               new Object[] { "container", ImmutableMap.of("key", "value") });

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container?restype=container&comp=metadata HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-meta-key: value\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   public void testGetBlob() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("getBlob", String.class, String.class, GetOptions[].class);
      HttpRequest request = processor.createRequest(method, "container", "blob");

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/container/blob HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseBlobFromHeadersAndHttpContent.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnKeyNotFound.class);
   }

   public void testSetBlobMetadata() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("setBlobMetadata", String.class, String.class, Map.class);
      HttpRequest request = processor.createRequest(method, "container", "blob", ImmutableMap.of("key", "value"));

      assertRequestLineEquals(request,
               "PUT https://identity.blob.core.windows.net/container/blob?comp=metadata HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-meta-key: value\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   public AzureBlobProviderMetadata createProviderMetadata() {
      return new AzureBlobProviderMetadata();
   }
}
