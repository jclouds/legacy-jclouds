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

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.abiquo.server.core.event.EventsDto;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests annotation parsing of {@code EventAsyncApi}
 * 
 * @author Ignasi Barrera
 * @author Vivien Mahé
 */
@Test(groups = "unit", testName = "EventAsyncApiTest")
public class EventAsyncApiTest extends BaseAbiquoAsyncApiTest<EventAsyncApi> {
   public void testListEvents() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(EventAsyncApi.class, "listEvents");
      GeneratedHttpRequest request = processor.apply(Invocation.create(method, ImmutableList.of()));

      assertRequestLineEquals(request, "GET http://localhost/api/events HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: " + EventsDto.BASE_MEDIA_TYPE + "\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseXMLWithJAXB.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
