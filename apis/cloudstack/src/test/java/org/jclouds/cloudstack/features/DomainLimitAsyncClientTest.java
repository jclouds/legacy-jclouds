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

import org.jclouds.cloudstack.domain.ResourceLimit;
import org.jclouds.cloudstack.domain.ResourceLimit.ResourceType;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code DomainLimitAsyncClient}
 * 
 * @author Adrian
 */
@Test(groups = "unit", testName = "DomainLimitAsyncClientTest")
public class DomainLimitAsyncClientTest extends BaseCloudStackAsyncClientTest<DomainLimitAsyncClient> {

   public void testUpdateResourceLimit() throws SecurityException, NoSuchMethodException, IOException {
      Method method = DomainLimitAsyncClient.class.getMethod("updateResourceLimit", ResourceLimit.class);
      HttpRequest httpRequest = processor.createRequest(method,
            ResourceLimit.builder().resourceType(ResourceType.SNAPSHOT).account("foo").domainId("100").max(101).build());

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=updateResourceLimit&resourcetype=3&account=foo&domainid=100&max=101 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<DomainLimitAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<DomainLimitAsyncClient>>() {
      };
   }
}
