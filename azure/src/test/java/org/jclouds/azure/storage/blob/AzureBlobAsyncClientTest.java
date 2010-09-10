/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.azure.storage.blob;

import static org.jclouds.azure.storage.blob.options.CreateContainerOptions.Builder.withPublicAcl;
import static org.jclouds.azure.storage.options.ListOptions.Builder.maxResults;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.jclouds.azure.storage.blob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azure.storage.blob.functions.ReturnFalseIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.blob.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.blob.xml.ContainerNameEnumerationResultsHandler;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.functions.ReturnNullOnContainerNotFound;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AzureBlobAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "azureblob.AzureBlobAsyncClientTest")
public class AzureBlobAsyncClientTest extends RestClientTest<AzureBlobAsyncClient> {

   public void testListContainers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", ListOptions[].class);
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "GET https://identity.blob.core.windows.net/?comp=list HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AccountNameEnumerationResultsHandler.class);
      assertExceptionParserClassEquals(method, null);

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
      assertExceptionParserClassEquals(method, null);
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
      assertExceptionParserClassEquals(method, ReturnFalseIfContainerAlreadyExists.class);
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
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class,
            CreateContainerOptions[].class);
      HttpRequest request = processor.createRequest(method, "container", withPublicAcl().withMetadata(
            ImmutableMultimap.of("foo", "bar")));

      assertRequestLineEquals(request,
            "PUT https://identity.blob.core.windows.net/container?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "x-ms-meta-foo: bar\nx-ms-prop-publicaccess: true\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", CreateContainerOptions[].class);

      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteRootContainer");
      HttpRequest request = processor.createRequest(method);

      assertRequestLineEquals(request,
            "DELETE https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "x-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnTrueOn404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", CreateContainerOptions[].class);
      HttpRequest request = processor.createRequest(method, withPublicAcl().withMetadata(
            ImmutableMultimap.of("foo", "bar")));

      assertRequestLineEquals(request, "PUT https://identity.blob.core.windows.net/$root?restype=container HTTP/1.1");
      assertNonPayloadHeadersEqual(request,
            "x-ms-meta-foo: bar\nx-ms-prop-publicaccess: true\nx-ms-version: 2009-09-19\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReturnTrueIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnFalseIfContainerAlreadyExists.class);
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
      assertExceptionParserClassEquals(method, null);
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
      assertExceptionParserClassEquals(method, null);
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
      assertExceptionParserClassEquals(method, ReturnNullOnContainerNotFound.class);
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
      assertExceptionParserClassEquals(method, null);
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
      assertExceptionParserClassEquals(method, null);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AzureBlobAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("azureblob", "identity", "credential", new Properties());
   }
}
