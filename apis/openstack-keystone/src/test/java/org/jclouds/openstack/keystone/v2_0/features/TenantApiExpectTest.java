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
package org.jclouds.openstack.keystone.v2_0.features;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestApiExpectTest;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rest.AuthorizationException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of TenantApi
 * 
 * @author Adam Lowe
 */
@Test(testName = "TenantApiExpectTest")
public class TenantApiExpectTest extends BaseKeystoneRestApiExpectTest<KeystoneApi> {

   public TenantApiExpectTest(){
      endpoint = "https://csnode.jclouds.org:35357";
   }

   Set<Tenant> expectedTenants = ImmutableSet.of(Tenant.builder().name("demo").id("05d1dc7af71646deba64cfc17b81bec0")
            .build(), Tenant.builder().name("admin").id("7aa2e17ec29f44d193c48feaba0852cc").build());

   public void testListTenants() {
      TenantApi api = requestsSendResponses(
               keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants").build(),
               HttpResponse.builder().statusCode(200).payload(
                        payloadFromResourceWithContentType("/tenant_list.json", APPLICATION_JSON)).build())
               .getTenantApi().get();

      assertEquals(api.list().concat().toSet(), expectedTenants);
   }
   
   public void testListTenantsPage() {
      TenantApi api = requestsSendResponses(
               keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants").build(),
               HttpResponse.builder().statusCode(200).payload(
                        payloadFromResourceWithContentType("/tenant_list.json", APPLICATION_JSON)).build())
               .getTenantApi().get();
      Set<? extends Tenant> tenants = api.list(new PaginationOptions()).toSet();
      assertNotNull(tenants);
      assertFalse(tenants.isEmpty());

      assertEquals(tenants, expectedTenants);
   }

   // this is not a compatible format of json per:
   // http://docs.openstack.org/api/openstack-identity-service/2.0/content/Paginated_Collections-d1e325.html
   @Test(enabled = false)
   public void testListTenantsATT() {
      TenantApi api = requestsSendResponses(
            keystoneAuthWithUsernameAndPasswordAndTenantName,
            responseWithKeystoneAccess,
            authenticatedGET().endpoint(endpoint + "/v2.0/tenants").build(),
            HttpResponse.builder().statusCode(200).payload(
                  payloadFromResourceWithContentType("/tenant_list_att.json", APPLICATION_JSON)).build())
            .getTenantApi().get();

      Set<Tenant> expected = ImmutableSet.of(Tenant.builder().name("this-is-a-test").id("14").description("None").build());

      assertEquals(api.list().concat().toSet(), expected);
   }
   
   // this is not a compatible format of json per:
   // http://docs.openstack.org/api/openstack-identity-service/2.0/content/Paginated_Collections-d1e325.html
   @Test(enabled = false)
   public void testListTenantsFailNotFound() {
      TenantApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants").build(), HttpResponse.builder().statusCode(404).build())
               .getTenantApi().get();
      assertTrue(api.list().isEmpty());
   }

   public void testGetTenant() {
      TenantApi api = requestsSendResponses(
               keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants/013ba41150a14830bec85ffe93353bcc").build(),
               HttpResponse.builder().statusCode(200).payload(
                        payloadFromResourceWithContentType("/tenant_details.json", APPLICATION_JSON)).build())
               .getTenantApi().get();
      Tenant tenant = api.get("013ba41150a14830bec85ffe93353bcc");
      assertNotNull(tenant);
      assertEquals(tenant, Tenant.builder().id("013ba41150a14830bec85ffe93353bcc").name("admin").build());
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testListTenantsFailNotAuthorized() {
      TenantApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants/013ba41150a14830bec85ffe93353bcc").build(),
               HttpResponse.builder().statusCode(401).build()).getTenantApi().get();
      api.get("013ba41150a14830bec85ffe93353bcc");
   }

   public void testGetTenantByName() {
      TenantApi api = requestsSendResponses(
               keystoneAuthWithUsernameAndPasswordAndTenantName,
               responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants?name=admin").build(),
               HttpResponse.builder().statusCode(200).payload(
                        payloadFromResourceWithContentType("/tenant_details.json", APPLICATION_JSON)).build())
               .getTenantApi().get();
      Tenant tenant = api.getByName("admin");
      assertNotNull(tenant);
      assertEquals(tenant, Tenant.builder().id("013ba41150a14830bec85ffe93353bcc").name("admin").build());
   }

   public void testGetTenantByNameFailNotFound() {
      TenantApi api = requestsSendResponses(keystoneAuthWithUsernameAndPasswordAndTenantName, responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants?name=admin").build(),
               HttpResponse.builder().statusCode(404).build()).getTenantApi().get();
      assertNull(api.getByName("admin"));
   }

}
