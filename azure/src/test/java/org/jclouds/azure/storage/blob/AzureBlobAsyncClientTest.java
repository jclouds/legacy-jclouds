/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.azure.storage.blob.functions.ParseContainerPropertiesFromHeaders;
import org.jclouds.azure.storage.blob.functions.ReturnFalseIfContainerAlreadyExists;
import org.jclouds.azure.storage.blob.options.CreateContainerOptions;
import org.jclouds.azure.storage.blob.options.ListBlobsOptions;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.blobstore.functions.ReturnNullOnContainerNotFound;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnTrueIf2xx;
import org.jclouds.http.functions.ReturnTrueOn404;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
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

   public void testListContainers() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", Array.newInstance(
               ListOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/");
      assertEquals(request.getEndpoint().getQuery(), "comp=list");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListContainersOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listContainers", Array.newInstance(
               ListOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] { maxResults(1).marker(
               "marker").prefix("prefix") });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/");
      assert request.getEndpoint().getQuery().contains("comp=list");
      assert request.getEndpoint().getQuery().contains("marker=marker");
      assert request.getEndpoint().getQuery().contains("maxresults=1");
      assert request.getEndpoint().getQuery().contains("prefix=prefix");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testCreateContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class, Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] { "container" });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(request.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testDeleteContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteContainer", String.class);

      HttpRequest request = processor.createRequest(method, new Object[] { "container" });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnVoidOnNotFoundOr404.class);
   }

   public void testCreateContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createContainer", String.class, Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] { "container",
               withPublicAcl().withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 4);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(request.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(request.getHeaders().get("x-ms-prop-publicaccess"), Collections
               .singletonList("true"));
      assertEquals(request.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testCreateRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/$root");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 2);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(request.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testDeleteRootContainer() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("deleteRootContainer");

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/$root");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.DELETE);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnTrueOn404.class);
   }

   public void testCreateRootContainerOptions() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("createRootContainer", Array
               .newInstance(CreateContainerOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] { withPublicAcl()
               .withMetadata(ImmutableMultimap.of("foo", "bar")) });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/$root");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 4);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(request.getHeaders().get("x-ms-meta-foo"), Collections.singletonList("bar"));
      assertEquals(request.getHeaders().get("x-ms-prop-publicaccess"), Collections
               .singletonList("true"));
      assertEquals(request.getHeaders().get("Content-Length"), Collections.singletonList("0"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ReturnTrueIf2xx.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnFalseIfContainerAlreadyExists.class);
   }

   public void testListBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", String.class, Array
               .newInstance(ListBlobsOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] { "container" });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container");
      assertEquals(request.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testListRootBlobs() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("listBlobs", Array.newInstance(
               ListBlobsOptions.class, 0).getClass());

      HttpRequest request = processor.createRequest(method, new Object[] {});
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/$root");
      assertEquals(request.getEndpoint().getQuery(), "restype=container&comp=list");
      assertEquals(request.getMethod(), HttpMethod.GET);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(), ParseSax.class);
      // TODO check generic type of response parser
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testContainerProperties() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("getContainerProperties", String.class);

      HttpRequest request = processor.createRequest(method, new Object[] { "container" });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container");
      assertEquals(request.getEndpoint().getQuery(), "restype=container");
      assertEquals(request.getMethod(), HttpMethod.HEAD);
      assertEquals(request.getHeaders().size(), 1);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(processor.createResponseParser(method, request).getClass(),
               ParseContainerPropertiesFromHeaders.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               ReturnNullOnContainerNotFound.class);
   }

   public void testSetResourceMetadata() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("setResourceMetadata", String.class,
               Map.class);

      HttpRequest request = processor.createRequest(method, new Object[] { "container",
               ImmutableMap.of("key", "value") });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container");
      assertEquals(request.getEndpoint().getQuery(), "restype=container&comp=metadata");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 3);
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("0"));
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(request.getHeaders().get("x-ms-meta-key"), Collections.singletonList("value"));

      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
   }

   public void testSetBlobMetadata() throws SecurityException, NoSuchMethodException {
      Method method = AzureBlobAsyncClient.class.getMethod("setBlobMetadata", String.class,
               String.class, Map.class);
      HttpRequest request = processor.createRequest(method, new Object[] { "container", "blob",
               ImmutableMap.of("key", "value") });
      assertEquals(request.getEndpoint().getHost(), "identity.blob.core.windows.net");
      assertEquals(request.getEndpoint().getPath(), "/container/blob");
      assertEquals(request.getEndpoint().getQuery(), "comp=metadata");
      assertEquals(request.getMethod(), HttpMethod.PUT);
      assertEquals(request.getHeaders().size(), 3);
      assertEquals(request.getHeaders().get("x-ms-version"), Collections
               .singletonList("2009-09-19"));
      assertEquals(request.getHeaders().get(HttpHeaders.CONTENT_LENGTH), Collections
               .singletonList("0"));
      assertEquals(request.getHeaders().get("x-ms-meta-key"), Collections.singletonList("value"));

      assertEquals(processor.createResponseParser(method, request).getClass(),
               CloseContentAndReturn.class);
      assertEquals(processor
               .createExceptionParserOrThrowResourceNotFoundOn404IfNoAnnotation(method).getClass(),
               MapHttp4xxCodesToExceptions.class);
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
      return new RestContextFactory().createContextSpec("azureblob", "identity", "credential",
               new Properties());
   }
}
