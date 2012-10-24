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

package org.jclouds.abiquo.rest.internal;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.abiquo.features.BaseAbiquoAsyncApiTest;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code AbiquoHttpAsyncApi}.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AbiquoHttpAsyncClientTest")
public class AbiquoHttpAsyncClientTest extends BaseAbiquoAsyncApiTest<AbiquoHttpAsyncClient> {
   public void testGet() throws SecurityException, NoSuchMethodException, IOException {
      RESTLink link = new RESTLink("edit", "http://foo/bar");
      link.setType(DatacentersDto.BASE_MEDIA_TYPE);

      Method method = AbiquoHttpAsyncClient.class.getMethod("get", RESTLink.class);
      GeneratedHttpRequest request = processor.createRequest(method, link);

      assertRequestLineEquals(request, "GET http://foo/bar HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + DatacentersDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, IdentityFunction.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AbiquoHttpAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AbiquoHttpAsyncClient>>() {
      };
   }

}
