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

import java.lang.reflect.Method;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DomainAccountAsyncClient}
 *
 * @author Adrian
 */
@Test(groups = "unit", testName = "DomainAccountAsyncClientTest")
public class DomainAccountAsyncClientTest extends BaseCloudStackAsyncClientTest<DomainAccountAsyncClient> {

   public void testEnableAccount() throws Exception {
      Method method = DomainAccountAsyncClient.class.getMethod("enableAccount", String.class, String.class);
      HttpRequest httpRequest = processor.createRequest(method, "goo", "2");

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=enableAccount&account=goo&domainid=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDisableAccount() throws Exception {
      Method method = DomainAccountAsyncClient.class.getMethod("disableAccount", String.class, String.class, boolean.class);
      HttpRequest httpRequest = processor.createRequest(method, "1", "2", true);

      assertRequestLineEquals(httpRequest,
         "GET http://localhost:8080/client/api?response=json&command=disableAccount&account=1&lock=true&domainid=2 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
