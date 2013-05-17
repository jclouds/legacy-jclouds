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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseExtensionListTest;
import org.jclouds.openstack.nova.v2_0.parse.ParseExtensionTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests annotation parsing of {@code ExtensionAsyncApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ExtensionApiExpectTest")
public class ExtensionApiExpectTest extends BaseNovaApiExpectTest {

   public void testListExtensionsWhenResponseIs2xx() throws Exception {
      HttpRequest listExtensions = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/extensions")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listExtensionsResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/extension_list.json")).build();

      NovaApi apiWhenExtensionsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listExtensions, listExtensionsResponse);

      assertEquals(apiWhenExtensionsExist.getConfiguredZones(), ImmutableSet.of("az-1.region-a.geo-1", "az-2.region-a.geo-1", "az-3.region-a.geo-1"));

      assertEquals(apiWhenExtensionsExist.getExtensionApiForZone("az-1.region-a.geo-1").list().toString(),
            new ParseExtensionListTest().expected().toString());
   }

   public void testListExtensionsWhenReponseIs404IsEmpty() throws Exception {
      HttpRequest listExtensions = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/extensions")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse listExtensionsResponse = HttpResponse.builder().statusCode(404).build();

      NovaApi apiWhenNoServersExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, listExtensions, listExtensionsResponse);

      assertTrue(apiWhenNoServersExist.getExtensionApiForZone("az-1.region-a.geo-1").list().isEmpty());
   }

   // TODO: gson deserializer for Multimap
   public void testGetExtensionByAliasWhenResponseIs2xx() throws Exception {

      HttpRequest getExtension = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/extensions/RS-PIE")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getExtensionResponse = HttpResponse.builder().statusCode(200)
            .payload(payloadFromResource("/extension_details.json")).build();

      NovaApi apiWhenExtensionsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getExtension, getExtensionResponse);

      assertEquals(apiWhenExtensionsExist.getExtensionApiForZone("az-1.region-a.geo-1").get("RS-PIE")
            .toString(), new ParseExtensionTest().expected().toString());
   }

   public void testGetExtensionByAliasWhenResponseIs404() throws Exception {
      HttpRequest getExtension = HttpRequest
            .builder()
            .method("GET")
            .endpoint("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/extensions/RS-PIE")
            .addHeader("Accept", "application/json")
            .addHeader("X-Auth-Token", authToken).build();

      HttpResponse getExtensionResponse = HttpResponse.builder().statusCode(404)
            .payload(payloadFromResource("/extension_details.json")).build();

      NovaApi apiWhenNoExtensionsExist = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, getExtension, getExtensionResponse);

      assertNull(apiWhenNoExtensionsExist.getExtensionApiForZone("az-1.region-a.geo-1").get("RS-PIE"));

   }

}
