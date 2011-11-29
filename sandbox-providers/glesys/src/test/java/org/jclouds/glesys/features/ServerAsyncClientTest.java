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

import org.jclouds.glesys.functions.MergeArgumentsAndReturnServerDetails;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code ServerAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ServerAsyncClientTest extends BaseGleSYSAsyncClientTest<ServerAsyncClient> {

   public void testListServers() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ServerAsyncClient.class.getMethod("listServers");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest, "GET https://api.glesys.com/server/list/format/json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetServer() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ServerAsyncClient.class.getMethod("getServerDetails", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "abcd");

      assertRequestLineEquals(httpRequest,
            "GET https://api.glesys.com/server/details/serverid/abcd/format/json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, MergeArgumentsAndReturnServerDetails.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ServerAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ServerAsyncClient>>() {
      };
   }
}
