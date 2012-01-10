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
package org.jclouds.glesys.features;

import com.google.common.collect.ImmutableSet;
import org.jclouds.glesys.GleSYSClient;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.options.DomainAddOptions;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests annotation parsing of {@code DomainAsyncClient}
 *
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "DomainAsyncClientTest")
public class DomainClientExpectTest extends BaseGleSYSClientExpectTest<DomainClient> {
   public DomainClientExpectTest() {
      remoteServicePrefix = "domain";
   }

   public void testListDomains() throws Exception {
      DomainClient client = createMock("list", "POST", 200, "/domain_list.json");
      assertEquals(client.listDomains(), ImmutableSet.<Domain>of(
            Domain.builder().domain("adamlowe.net").createTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2011-12-20 10:58:51")).build()
      ));

      // check not found response
      client = createMock("list", "POST", 404, "/domain_list.json");
      assertTrue(client.listDomains().isEmpty());
   }

   public void testAddDomain() throws Exception {
      createMock("add", "POST", 200, null, entry("name", "cl66666_x")).addDomain("cl66666_x");
      DomainAddOptions options = (DomainAddOptions) DomainAddOptions.Builder.primaryNameServer("ns1.somewhere.x").expire(1).minimum(1).refresh(1).
            responsiblePerson("Tester").retry(1).ttl(1);      createMock("add", "POST", 200, null, entry("name", "cl66666_x"));
      createMock("add", "POST", 200, null, entry("name", "cl66666_x"), options).addDomain("cl66666_x", options);
   }

   public void testEditDomain() throws Exception {
      createMock("edit", "POST", 200, null, entry("domain", "x")).editDomain("x");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testEditDomainNotFound() throws Exception {
      createMock("edit", "POST", 404, null, entry("domain", "x")).editDomain("x");
   }
   
   public void testDeleteDomain() throws Exception {
      createMock("delete", "POST", 200, null, entry("domain", "cl666666someuser")).deleteDomain("cl666666someuser");
   }

   @Test(expectedExceptions = {ResourceNotFoundException.class})
   public void testDeleteDomainNotFound() throws Exception {
      createMock("delete", "POST", 404, null, entry("domain", "cl666666someuser")).deleteDomain("cl666666someuser");
   }
 
   @Override
   protected DomainClient getClient(GleSYSClient gleSYSClient) {
      return gleSYSClient.getDomainClient();
   }
}
