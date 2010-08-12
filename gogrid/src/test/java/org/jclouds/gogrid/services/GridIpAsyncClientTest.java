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

package org.jclouds.gogrid.services;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.gogrid.domain.IpType;
import org.jclouds.gogrid.functions.ParseIpListFromJsonResponse;
import org.jclouds.gogrid.options.GetIpListOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * @author Oleksiy Yarmula
 */
public class GridIpAsyncClientTest extends BaseGoGridAsyncClientTest<GridIpAsyncClient> {

   @Test
   public void testGetIpListWithOptions() throws NoSuchMethodException, IOException {
      Method method = GridIpAsyncClient.class.getMethod("getIpList", GetIpListOptions[].class);
      GeneratedHttpRequest<GridIpAsyncClient> httpRequest = processor.createRequest(method, new GetIpListOptions()
            .onlyUnassigned().onlyWithType(IpType.PUBLIC));

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/ip/list?v=1.5&ip.state=Unassigned&"
            + "ip.type=Public HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseIpListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/ip/list?v=1.5&ip.state=Unassigned&"
            + "ip.type=Public&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Test
   public void testGetAssignedIpList() throws NoSuchMethodException, IOException {
      Method method = GridIpAsyncClient.class.getMethod("getAssignedIpList");
      GeneratedHttpRequest<GridIpAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://api.gogrid.com/api/grid/ip/list?v=1.5&ip.state=Assigned HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseIpListFromJsonResponse.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/ip/list?v=1.5&ip.state=Assigned&"
            + "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " + "HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<GridIpAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<GridIpAsyncClient>>() {
      };
   }
}
