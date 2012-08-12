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
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Tenant;
import org.jclouds.openstack.keystone.v2_0.internal.BaseKeystoneRestApiExpectTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests parsing and Guice wiring of ServiceApi
 * 
 * @author Adam Lowe
 */
@Test(testName = "ServiceApiExpectTest")
public class ServiceApiExpectTest extends BaseKeystoneRestApiExpectTest<KeystoneApi> {

   public void testListTenants() {
      ServiceApi api = requestsSendResponses(
               keystoneAuthWithUsernameAndPassword,
               responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants").build(),
               HttpResponse.builder().statusCode(200)
                        .payload(payloadFromResourceWithContentType("/tenant_list.json", APPLICATION_JSON)).build())
               .getServiceApi();
      Set<? extends Tenant> tenants = api.listTenants();
      assertNotNull(tenants);
      assertFalse(tenants.isEmpty());

      Set<Tenant> expected = ImmutableSet.of(Tenant.builder().name("demo").id("05d1dc7af71646deba64cfc17b81bec0")
               .build(), Tenant.builder().name("admin").id("7aa2e17ec29f44d193c48feaba0852cc").build());

      assertEquals(tenants, expected);
   }

   public void testListTenantsFailNotFound() {
      ServiceApi api = requestsSendResponses(keystoneAuthWithUsernameAndPassword, responseWithKeystoneAccess,
               authenticatedGET().endpoint(endpoint + "/v2.0/tenants").build(),
               HttpResponse.builder().statusCode(404).build()).getServiceApi();
      assertTrue(api.listTenants().isEmpty());
   }
}
