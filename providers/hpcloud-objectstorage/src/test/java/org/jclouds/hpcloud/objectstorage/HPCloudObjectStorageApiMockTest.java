/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.hpcloud.objectstorage;

import static org.testng.Assert.assertEquals;

import org.jclouds.hpcloud.objectstorage.internal.BaseHPCloudObjectStorageMockTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

@Test
public class HPCloudObjectStorageApiMockTest extends BaseHPCloudObjectStorageMockTest {

   String mixedSwiftVersions = "" //
         + "{\"access\": {\n" //
         + "  \"token\": {\n" //
         + "    \"expires\": \"2013-10-01T09:16:20.177Z\",\n" //
         + "    \"id\": \"HPAuth10_1234567890\",\n" //
         + "    \"tenant\": {\n" //
         + "      \"id\": \"12345678\",\n" //
         + "      \"name\": \"jclouds-project\"\n" //
         + "    }\n" //
         + "  },\n" //
         + "  \"user\": {\n" //
         + "    \"id\": \"12345678\",\n" //
         + "    \"name\": \"jclouds\"\n" //
         + "  },\n" //
         + "  \"serviceCatalog\": [\n" //
         + "    {\n" //
         + "      \"name\": \"Object Storage\",\n" //
         + "      \"type\": \"object-store\",\n" //
         + "      \"endpoints\": [\n" //
         + "        {\n" //
         + "          \"tenantId\": \"12345678\",\n" //
         + "          \"publicURL\": \"URL\\/v1\\/12345678\",\n" //
         + "          \"region\": \"region-a.geo-1\",\n" //
         + "          \"versionId\": \"1.0\",\n" //
         + "          \"versionInfo\": \"URL\\/v1.0\\/\",\n" //
         + "          \"versionList\": \"URL\"\n" //
         + "        },\n" //
         + "        {\n" //
         + "          \"tenantId\": \"12345678\",\n" //
         + "          \"publicURL\": \"URL\\/v1\\/12345678\",\n" //
         + "          \"region\": \"region-b.geo-1\",\n" //
         + "          \"versionId\": \"1\",\n" //
         + "          \"versionInfo\": \"URL\\/v1\\/\",\n" //
         + "          \"versionList\": \"URL\"\n" //
         + "      }]\n" //
         + "    }\n" //
         + "  ]\n" //
         + "}}";

   public void mixedVersionsInRegions() throws Exception {
      MockWebServer server = mockWebServer();
      server.enqueue(new MockResponse().setBody(mixedSwiftVersions));
      try {

         HPCloudObjectStorageApi api = api(server.getUrl("/").toString());

         assertEquals(api.getConfiguredRegions(), ImmutableSet.of("region-a.geo-1", "region-b.geo-1"));

         assertEquals(server.getRequestCount(), 1);
         RecordedRequest authRequest = server.takeRequest();
         assertEquals(authRequest.getRequestLine(), "POST /tokens HTTP/1.1");
      } finally {
         server.shutdown();
      }
   }
}
