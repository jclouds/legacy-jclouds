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

package org.jclouds.cloudstack.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudstack.domain.TemplateFilter;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code TemplateAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TemplateAsyncClientTest")
public class TemplateAsyncClientTest extends BaseCloudStackAsyncClientTest<TemplateAsyncClient> {
   public void testListTemplates() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TemplateAsyncClient.class.getMethod("listTemplates");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listTemplates&templatefilter=executable HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListTemplatesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TemplateAsyncClient.class.getMethod("listTemplates", ListTemplatesOptions.class);
      HttpRequest httpRequest = processor.createRequest(method, ListTemplatesOptions.Builder.accountInDomain(
               "accountId", "domainId").hypervisor("xen").filter(TemplateFilter.FEATURED));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listTemplates&account=accountId&domainid=domainId&hypervisor=xen&templatefilter=featured HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetTemplate() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TemplateAsyncClient.class.getMethod("getTemplate", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "id");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listTemplates&templatefilter=executable&id=id HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<TemplateAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<TemplateAsyncClient>>() {
      };
   }
}
