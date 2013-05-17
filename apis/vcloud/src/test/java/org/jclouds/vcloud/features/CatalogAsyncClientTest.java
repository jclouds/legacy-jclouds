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
package org.jclouds.vcloud.features;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.net.URI;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.vcloud.internal.BaseVCloudAsyncClientTest;
import org.jclouds.vcloud.options.CatalogItemOptions;
import org.jclouds.vcloud.xml.CatalogHandler;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code CatalogAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CatalogAsyncClientTest")
public class CatalogAsyncClientTest extends BaseVCloudAsyncClientTest<CatalogAsyncClient> {

   public void testCatalog() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CatalogAsyncClient.class, "getCatalog", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/catalog/1")));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalog/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCatalogInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CatalogAsyncClient.class, "findCatalogInOrgNamed", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("org", "catalog"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalog/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalog+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCatalogItemURI() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CatalogAsyncClient.class, "getCatalogItem", URI.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2")));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testFindCatalogItemInOrgCatalogNamed() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(CatalogAsyncClient.class, "findCatalogItemInOrgCatalogNamed", String.class,
               String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("org", "catalog", "item"));

      assertRequestLineEquals(request, "GET https://vcenterprise.bluelock.com/api/v1.0/catalogItem/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAddVAppTemplateOrMediaImageToCatalogAndNameItem() throws SecurityException, NoSuchMethodException,
            IOException {
      Invokable<?, ?> method = method(CatalogAsyncClient.class, "addVAppTemplateOrMediaImageToCatalogAndNameItem", URI.class,
               URI.class, String.class, CatalogItemOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of(URI.create("http://fooentity"), URI
               .create("https://vcenterprise.bluelock.com/api/v1.0/catalog/1"), "myname", CatalogItemOptions.Builder
               .description("mydescription")));

      assertRequestLineEquals(request,
               "POST https://vcenterprise.bluelock.com/api/v1.0/catalog/1/catalogItems HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.vmware.vcloud.catalogItem+xml\n");
      assertPayloadEquals(
               request,
               "<CatalogItem xmlns=\"http://www.vmware.com/vcloud/v1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"myname\" xsi:schemaLocation=\"http://www.vmware.com/vcloud/v1 http://vcloud.safesecureweb.com/ns/vcloud.xsd\"><Description>mydescription</Description><Entity href=\"http://fooentity\"/></CatalogItem>",
               "application/vnd.vmware.vcloud.catalogItem+xml", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CatalogItemHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

}
