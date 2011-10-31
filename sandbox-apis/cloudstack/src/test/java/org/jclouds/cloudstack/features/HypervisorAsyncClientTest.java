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

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudstack.functions.ParseNamesFromHttpResponse;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code HypervisorAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "HypervisorAsyncClientTest")
public class HypervisorAsyncClientTest extends BaseCloudStackAsyncClientTest<HypervisorAsyncClient> {

   public void testListHypervisors() throws SecurityException, NoSuchMethodException, IOException {
      Method method = HypervisorAsyncClient.class.getMethod("listHypervisors");
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listHypervisors HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseNamesFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListHypervisorsInZon() throws SecurityException, NoSuchMethodException, IOException {
      Method method = HypervisorAsyncClient.class.getMethod("listHypervisorsInZone", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 11);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listHypervisors&zoneid=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseNamesFromHttpResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<HypervisorAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<HypervisorAsyncClient>>() {
      };
   }
}
