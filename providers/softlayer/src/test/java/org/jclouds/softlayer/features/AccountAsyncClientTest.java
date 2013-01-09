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
package org.jclouds.softlayer.features;

import java.io.IOException;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.http.functions.ParseJson;
import com.google.common.reflect.Invokable;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests annotation parsing of {@code AccountAsyncClient}
 *
 * @author Jason King
 */
@Test(groups = "unit")
public class AccountAsyncClientTest extends BaseSoftLayerAsyncClientTest<AccountAsyncClient> {

   public void testGetActivePackages() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(AccountAsyncClient.class.getMethod("getActivePackages"));
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(
               httpRequest,
                       "GET https://api.softlayer.com/rest/v3/SoftLayer_Account/ActivePackages.json HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }
}
