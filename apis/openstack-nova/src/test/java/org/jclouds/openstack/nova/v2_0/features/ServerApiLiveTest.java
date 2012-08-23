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
package org.jclouds.openstack.nova.v2_0.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.*;

import java.util.Set;

import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.internal.BaseNovaApiLiveTest;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.predicates.LinkPredicates;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@link ServerApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseNovaApiLiveTest {

    @Test(description = "GET /v${apiVersion}/{tenantId}/servers")
    public void testListServers() throws Exception {
       for (String zoneId : zones) {
          ServerApi api = novaContext.getApi().getServerApiForZone(zoneId);
          Set<? extends Resource> response = api.listServers();
          assertNotNull(response);
          assertFalse(response.isEmpty());
          assert null != response;
          assertTrue(response.size() >= 0);
          for (Resource server : response) {
             checkResource(server);
          }
       }
    }

    @Test(description = "GET /v${apiVersion}/{tenantId}/servers/detail")
    public void testListServersInDetail() throws Exception {
       for (String zoneId : zones) {
          ServerApi api = novaContext.getApi().getServerApiForZone(zoneId);
          Set<? extends Server> response = api.listServersInDetail();
          assertNotNull(response);
          assertFalse(response.isEmpty());
          for (Server server : response) {
             checkServer(server);
          }
       }
    }

    @Test(description = "GET /v${apiVersion}/{tenantId}/servers/{id}", dependsOnMethods = { "testListServersInDetail" })
    public void testGetServerById() throws Exception {
       for (String zoneId : zones) {
          ServerApi api = novaContext.getApi().getServerApiForZone(zoneId);
          Set<? extends Resource> response = api.listServers();
          for (Resource server : response) {
             Server details = api.getServer(server.getId());
             assertEquals(details.getId(), server.getId());
             assertEquals(details.getName(), server.getName());
             assertEquals(details.getLinks(), server.getLinks());
             checkServer(details);
          }
       }
    }

    private void checkResource(Resource resource) {
       assertNotNull(resource.getId());
       assertNotNull(resource.getName());
       assertNotNull(resource.getLinks());
       assertTrue(Iterables.any(resource.getLinks(), LinkPredicates.relationEquals(Relation.SELF)));
    }

    private void checkServer(Server server) {
       checkResource(server);
       assertFalse(server.getAddresses().isEmpty());
    }
}
