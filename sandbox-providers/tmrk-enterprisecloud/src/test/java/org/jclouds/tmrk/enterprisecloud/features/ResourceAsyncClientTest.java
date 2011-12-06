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
package org.jclouds.tmrk.enterprisecloud.features;

import com.google.inject.TypeLiteral;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Tests annotation parsing of {@code ResourceAsyncClient}
 * 
 * @author Jason King
 */
@Test(groups = "unit", testName = "ResourceAsyncClient")
public class ResourceAsyncClientTest extends BaseTerremarkEnterpriseCloudAsyncClientTest<ResourceAsyncClient> {

   public void testGetResourceSummaries() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = ResourceAsyncClient.class.getMethod("getResourceSummaries", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/computepools/environments/77/resourcesummarylist"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/computepools/environments/77/resourcesummarylist HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.computePoolResourceSummary; type=collection\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetResourceSummary() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = ResourceAsyncClient.class.getMethod("getResourceSummary", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, new URI("/cloudapi/ecloud/computepools/89/resourcesummary"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/computepools/89/resourcesummary HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.computePoolResourceSummary\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetComputePoolCpuUsage() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = ResourceAsyncClient.class.getMethod("getComputePoolCpuUsage", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, URI.create("/cloudapi/ecloud/computepools/89/usage/cpu"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/computepools/89/usage/cpu HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.computePoolCpuUsage\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetComputePoolCpuUsageDetail() throws SecurityException, NoSuchMethodException, IOException, URISyntaxException {
      Method method = ResourceAsyncClient.class.getMethod("getComputePoolCpuUsageDetail", URI.class);
      HttpRequest httpRequest = processor.createRequest(method, URI.create("/cloudapi/ecloud/computepools/89/usage/cpu/details?time=2011-12-05t10%3a10%3a00z"));

      assertRequestLineEquals(httpRequest, "GET https://services-beta.enterprisecloud.terremark.com/cloudapi/ecloud/computepools/89/usage/cpu/details?time=2011-12-05t10%3a10%3a00z HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/vnd.tmrk.cloud.computePoolCpuUsageDetail\nx-tmrk-version: 2011-07-01\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseXMLWithJAXB.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ResourceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ResourceAsyncClient>>() {
      };
   }
}
