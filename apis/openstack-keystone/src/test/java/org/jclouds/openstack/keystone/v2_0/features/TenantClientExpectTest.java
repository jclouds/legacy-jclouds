/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 1.1 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-1.1
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.keystone.v2_0.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestClientExpectTest;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of TenantClient
 * 
 * @author Adam Lowe
 */
@Test(testName = "TenantClientExpectTest")
public class TenantClientExpectTest extends BaseKeystoneRestClientExpectTest<KeystoneClient> {

   public TenantClientExpectTest(){
      endpoint = "https://csnode.jclouds.org:35357";
   }
   
   public void testListTenants() {
      TenantClient client = requestsSendResponses(
               keystoneAuthWithUsernameAndPassword,
               responseWithKeystoneAccess,
               standardRequestBuilder(endpoint + "/v2.0/tenants").build(),
               standardResponseBuilder(200).payload(
                        payloadFromResourceWithContentType("/tenant_list.json", APPLICATION_JSON)).build())
               .getTenantClient().get();
      Set<Tenant> tenants = client.list();
      assertNotNull(tenants);
      assertFalse(tenants.isEmpty());

      Set<Tenant> expected = ImmutableSet.of(Tenant.builder().name("demo").id("05d1dc7af71646deba64cfc17b81bec0")
               .build(), Tenant.builder().name("admin").id("7aa2e17ec29f44d193c48feaba0852cc").build());

      assertEquals(tenants, expected);
   }

   public void testListTenantsATT() {
      TenantClient client = requestsSendResponses(
            keystoneAuthWithUsernameAndPassword,
            responseWithKeystoneAccess,
            standardRequestBuilder(endpoint + "/v2.0/tenants").build(),
            standardResponseBuilder(200).payload(
                  payloadFromResourceWithContentType("/tenant_list_att.json", APPLICATION_JSON)).build())
            .getTenantClient().get();
      Set<Tenant> tenants = client.list();
      assertNotNull(tenants);
      assertFalse(tenants.isEmpty());

      Set<Tenant> expected = ImmutableSet.of(Tenant.builder().name("this-is-a-test").id("14").description("None").build());

      assertEquals(tenants, expected);
   }
   
   public void testListTenantsFailNotFound() {
      TenantClient client = requestsSendResponses(keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
               standardRequestBuilder(endpoint + "/v2.0/tenants").build(), standardResponseBuilder(404).build())
               .getTenantClient().get();
      assertTrue(client.list().isEmpty());
   }

   public void testGetTenant() {
      TenantClient client = requestsSendResponses(
               keystoneAuthWithUsernameAndPassword,
               responseWithKeystoneAccess,
               standardRequestBuilder(endpoint + "/v2.0/tenants/013ba41150a14830bec85ffe93353bcc").build(),
               standardResponseBuilder(200).payload(
                        payloadFromResourceWithContentType("/tenant_details.json", APPLICATION_JSON)).build())
               .getTenantClient().get();
      Tenant tenant = client.get("013ba41150a14830bec85ffe93353bcc");
      assertNotNull(tenant);
      assertEquals(tenant, Tenant.builder().id("013ba41150a14830bec85ffe93353bcc").name("admin").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListTenantsFailNotAuthorized() {
      TenantClient client = requestsSendResponses(keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
               standardRequestBuilder(endpoint + "/v2.0/tenants/013ba41150a14830bec85ffe93353bcc").build(),
               standardResponseBuilder(401).build()).getTenantClient().get();
      client.get("013ba41150a14830bec85ffe93353bcc");
   }

   public void testGetTenantByName() {
      TenantClient client = requestsSendResponses(
               keystoneAuthWithUsernameAndPassword,
               responseWithKeystoneAccess,
               standardRequestBuilder(endpoint + "/v2.0/tenants?name=admin").build(),
               standardResponseBuilder(200).payload(
                        payloadFromResourceWithContentType("/tenant_details.json", APPLICATION_JSON)).build())
               .getTenantClient().get();
      Tenant tenant = client.getByName("admin");
      assertNotNull(tenant);
      assertEquals(tenant, Tenant.builder().id("013ba41150a14830bec85ffe93353bcc").name("admin").build());
   }

   public void testGetTenantByNameFailNotFound() {
      TenantClient client = requestsSendResponses(keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
               standardRequestBuilder(endpoint + "/v2.0/tenants?name=admin").build(),
               standardResponseBuilder(404).build()).getTenantClient().get();
      assertNull(client.getByName("admin"));
   }

}
