/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features;

import static org.jclouds.abiquo.domain.DomainUtils.withHeader;
import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.abiquo.domain.ConfigResources;
import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.domain.config.options.PropertyOptions;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.server.core.appslibrary.CategoriesDto;
import com.abiquo.server.core.appslibrary.CategoryDto;
import com.abiquo.server.core.config.LicenseDto;
import com.abiquo.server.core.config.LicensesDto;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.PrivilegeDto;
import com.abiquo.server.core.enterprise.PrivilegesDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests annotation parsing of {@code AdminAsyncApi}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Test(groups = "unit", testName = "ConfigAsyncApiTest")
public class ConfigAsyncApiTest extends BaseAbiquoAsyncApiTest<ConfigAsyncApi> {
   /*********************** License ***********************/

   public void testListLicenses() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "listLicenses");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/licenses HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LicensesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListLicenseWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "listLicenses", LicenseOptions.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(LicenseOptions.builder().active(true).build())));

      assertRequestLineEquals(request, "GET http://localhost/api/config/licenses?active=true HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LicensesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddLicense() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "addLicense", LicenseDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(ConfigResources.licensePost())));

      assertRequestLineEquals(request, "POST http://localhost/api/config/licenses HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + LicenseDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(ConfigResources.licensePostPayload()), LicenseDto.class,
            LicenseDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRemoveLicense() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "removeLicense", LicenseDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(ConfigResources.licensePut())));

      assertRequestLineEquals(request, "DELETE http://localhost/api/config/licenses/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Privilege ***********************/

   public void testListPrivileges() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "listPrivileges");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/privileges HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PrivilegesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetPrivilege() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "getPrivilege", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/privileges/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + PrivilegeDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   /*********************** System Properties ***********************/

   public void testListSystemProperties() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "listSystemProperties");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/properties HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + SystemPropertiesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testListSystemPropertiesWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "listSystemProperties", PropertyOptions.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(PropertyOptions.builder().component("api").build())));

      assertRequestLineEquals(request, "GET http://localhost/api/config/properties?component=api HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + SystemPropertiesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateSystemProperty() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "updateSystemProperty", SystemPropertyDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(ConfigResources.propertyPut())));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/properties/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + SystemPropertyDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(ConfigResources.propertyPutPayload()), SystemPropertyDto.class,
            SystemPropertyDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   /*********************** Category ***********************/

   public void testListCategories() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "listCategories");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/config/categories HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CategoriesDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetCategory() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "getCategory", Integer.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.<Object> of(1)));

      assertRequestLineEquals(request, "GET http://localhost/api/config/categories/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CategoryDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateCategory() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "createCategory", CategoryDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(ConfigResources.categoryPost())));

      assertRequestLineEquals(request, "POST http://localhost/api/config/categories HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CategoryDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(ConfigResources.categoryPostPayload()), CategoryDto.class,
            CategoryDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testUpdateCategory() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "updateCategory", CategoryDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(ConfigResources.categoryPut())));

      assertRequestLineEquals(request, "PUT http://localhost/api/config/categories/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + CategoryDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, withHeader(ConfigResources.categoryPutPayload()), CategoryDto.class,
            CategoryDto.BASE_MEDIA_TYPE, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDeleteCategory() throws SecurityException, NoSuchMethodException {
      Invokable<?, ?> method = method(ConfigAsyncApi.class, "deleteCategory", CategoryDto.class);
      GeneratedHttpRequest request = processor.apply(Invocation.create(method,
            ImmutableList.<Object> of(ConfigResources.categoryPut())));

      assertRequestLineEquals(request, "DELETE http://localhost/api/config/categories/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
