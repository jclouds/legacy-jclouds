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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.ecloud.BaseTerremarkECloudAsyncClientTest;
import org.jclouds.trmk.vcloud_0_8.xml.DataCentersHandler;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code DataCenterOperationsAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "DataCenterOperationsAsyncClientTest")
public class DataCenterOperationsAsyncClientTest extends
      BaseTerremarkECloudAsyncClientTest<DataCenterOperationsAsyncClient> {

   public void testlistDataCenters() throws SecurityException, NoSuchMethodException, IOException {
      Method method = DataCenterOperationsAsyncClient.class.getMethod("listDataCenters", URI.class);
      HttpRequest request = processor
            .createRequest(
                  method,
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/dataCenters"));

      assertRequestLineEquals(request,
            "GET https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/dataCenters HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.dataCentersList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DataCentersHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testlistDataCentersInOrg() throws SecurityException, NoSuchMethodException, IOException {
      Method method = DataCenterOperationsAsyncClient.class.getMethod("listDataCentersInOrg", URI.class);
      HttpRequest request = processor.createRequest(method,
            URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

      assertRequestLineEquals(request, "GET https://vcloud.safesecureweb.com/api/v0.8/datacentersList/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Accept: application/vnd.tmrk.ecloud.dataCentersList+xml\n");
      assertPayloadEquals(request, null, null, false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DataCentersHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

}
