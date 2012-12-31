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
package org.jclouds.trmk.ecloud.features;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import org.jclouds.Fallbacks.EmptyMapOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.ecloud.BaseTerremarkECloudAsyncClientTest;
import org.jclouds.trmk.ecloud.xml.TagNameToUsageCountHandler;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code TagOperationsAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "TagOperationsAsyncClientTest")
public class TagOperationsAsyncClientTest extends BaseTerremarkECloudAsyncClientTest<TagOperationsAsyncClient> {

   public void testgetTagNameToUsageCount() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagOperationsAsyncClient.class.getMethod("getTagNameToUsageCount", URI.class);
      HttpRequest request = processor
            .createRequest(
                  method,
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/deviceTags"));

      assertRequestLineEquals(request,
            "GET https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/deviceTags HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.tagsList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TagNameToUsageCountHandler.class);
      assertFallbackClassEquals(method, EmptyMapOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testgetTagNameToUsageCountInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = TagOperationsAsyncClient.class.getMethod("getTagNameToUsageCountInOrg", URI.class);
      HttpRequest request = processor.createRequest(method,
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/deviceTags/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.tagsList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TagNameToUsageCountHandler.class);
      assertFallbackClassEquals(method, EmptyMapOnNotFoundOr404.class);

      checkFilters(request);
   }

}
