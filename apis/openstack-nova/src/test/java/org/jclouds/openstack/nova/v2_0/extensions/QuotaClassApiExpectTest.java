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
package org.jclouds.openstack.nova.v2_0.extensions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.nova.v2_0.domain.QuotaClass;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiExpectTest;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

/**
 * Tests HostAdministrationApi guice wiring and parsing
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "QuotaClassApiExpectTest")
public class QuotaClassApiExpectTest extends BaseNovaApiExpectTest {

   public void testGetQuotas() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-class-sets/jcloudstestquotas");
      QuotaClassApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(200).payload(payloadFromResource("/quota_class.json")).build()).getQuotaClassExtensionForZone("az-1.region-a.geo-1").get();

      assertEquals(api.get("jcloudstestquotas"), getTestQuotas());
   }

   public void testGetQuotasFailsTenantNotFound() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-class-sets/jcloudstestquotas");
      QuotaClassApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            authenticatedGET().endpoint(endpoint).build(),
            HttpResponse.builder().statusCode(404).build()).getQuotaClassExtensionForZone("az-1.region-a.geo-1").get();
      assertNull(api.get("jcloudstestquotas"));
   }

   public void testUpdateQuotas() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-class-sets/myclass");
      QuotaClassApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().endpoint(endpoint).method("PUT")
                  .addHeader("X-Auth-Token", authToken)
                  .payload(payloadFromResourceWithContentType("/quota_class.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(200).build()).getQuotaClassExtensionForZone("az-1.region-a.geo-1").get();

      assertTrue(api.update("myclass", getTestQuotas()));
   }

   @Test(expectedExceptions = ResourceNotFoundException.class)
   public void testUpdateQuotasFailsNotFound() throws Exception {
      URI endpoint = URI.create("https://az-1.region-a.geo-1.compute.hpcloudsvc.com/v1.1/3456/os-quota-class-sets/jcloudstestquotas");
      QuotaClassApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess, extensionsOfNovaRequest, extensionsOfNovaResponse,
            HttpRequest.builder().endpoint(endpoint).method("PUT")
                  .addHeader("X-Auth-Token", authToken)
                  .payload(payloadFromResourceWithContentType("/quota_class.json", MediaType.APPLICATION_JSON))
                  .build(),
            HttpResponse.builder().statusCode(404).build()).getQuotaClassExtensionForZone("az-1.region-a.geo-1").get();

      api.update("jcloudstestquotas", getTestQuotas());
   }

   public static QuotaClass getTestQuotas() {
      return QuotaClass.builder()
            .metadataItems(128)
            .injectedFileContentBytes(10240)
            .injectedFiles(5)
            .gigabytes(1000)
            .ram(4096)
            .floatingIps(10)
            .securityGroups(10)
            .securityGroupRules(20)
            .instances(5)
            .keyPairs(100)
            .volumes(5)
            .cores(10)
            .id("jcloudstestquotas").build();
   }

}
