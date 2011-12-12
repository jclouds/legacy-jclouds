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
package org.jclouds.cloudstack.features;

import org.jclouds.cloudstack.options.ListAccountsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Tests behavior of {@code DomainAccountAsyncClient}
 *
 * @author Adrian
 */
@Test(groups = "unit", testName = "DomainAccountAsyncClientTest")
public class DomainAccountAsyncClientTest extends BaseCloudStackAsyncClientTest<DomainAccountAsyncClient> {

   public void testEnableAccount() throws Exception {
      Method method = DomainAccountAsyncClient.class.getMethod("enableAccount", long.class, long.class);
      HttpRequest httpRequest = processor.createRequest(method, 1L, 2L);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=enableAccount&account=1&domainid=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDisableAccount() throws Exception {
      Method method = DomainAccountAsyncClient.class.getMethod("disableAccount", long.class, long.class, boolean.class);
      HttpRequest httpRequest = processor.createRequest(method, 1L, 2L, true);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=disableAccount&account=1&lock=true&domainid=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<DomainAccountAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<DomainAccountAsyncClient>>() {
      };
   }
}
