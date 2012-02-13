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
package org.jclouds.glesys.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.glesys.internal.BaseGleSYSAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code IpAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "IpAsyncClientTest")
public class IpAsyncClientTest extends BaseGleSYSAsyncClientTest<IpAsyncClient> {

   public void testGetIpDetails() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IpAsyncClient.class.getMethod("getIpDetails", String.class);
      HttpRequest request = processor.createRequest(method, "31.192.227.37");

      assertRequestLineEquals(request,
               "GET https://api.glesys.com/ip/details/ipaddress/31.192.227.37/format/json HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/json\n");
      assertPayloadEquals(request, null, "application/xml", false);

      assertResponseParserClassEquals(method, request, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<IpAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<IpAsyncClient>>() {
      };
   }
}
