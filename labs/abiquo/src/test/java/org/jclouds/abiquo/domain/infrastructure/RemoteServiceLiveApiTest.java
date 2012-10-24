/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.infrastructure;

import static org.jclouds.abiquo.predicates.infrastructure.RemoteServicePredicates.type;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.domain.infrastructure.RemoteService.Builder;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link RemoteService} domain class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "RemoteServiceLiveApiTest")
public class RemoteServiceLiveApiTest extends BaseAbiquoApiLiveApiTest {
   public void testUpdate() {
      // Update the remote service
      RemoteService rs = env.datacenter.findRemoteService(type(RemoteServiceType.VIRTUAL_FACTORY));
      rs.setUri(rs.getUri());
      rs.update();

      // Recover the updated remote service
      RemoteServiceDto updated = env.infrastructureApi.getRemoteService(env.datacenter.unwrap(),
            RemoteServiceType.VIRTUAL_FACTORY);

      assertEquals(updated.getUri(), rs.getUri());
   }

   public void testDelete() {
      RemoteService rs = env.datacenter.findRemoteService(type(RemoteServiceType.BPM_SERVICE));
      rs.delete();

      // Recover the deleted remote service
      RemoteServiceDto deleted = env.infrastructureApi.getRemoteService(env.datacenter.unwrap(),
            RemoteServiceType.BPM_SERVICE);

      assertNull(deleted);

      URI endpoint = URI.create(env.context.getApiContext().getProviderMetadata().getEndpoint());

      // Restore rs
      RemoteService bpm = RemoteService.builder(env.context.getApiContext(), env.datacenter)
            .type(RemoteServiceType.BPM_SERVICE).ip(endpoint.getHost()).build();
      bpm.save();
   }

   public void testIsAvailableNonCheckeable() {
      RemoteService rs = env.datacenter.findRemoteService(type(RemoteServiceType.DHCP_SERVICE));
      assertTrue(rs.isAvailable());
   }

   public void testIsAvailable() {
      RemoteService rs = env.datacenter.findRemoteService(type(RemoteServiceType.NODE_COLLECTOR));
      assertTrue(rs.isAvailable());
   }

   public void testCreateRepeated() {
      RemoteService repeated = Builder.fromRemoteService(env.remoteServices.get(1)).build();

      try {
         repeated.save();
         fail("Should not be able to create duplicated remote services in the datacenter");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "RS-6");
      }
   }

   public void testListRemoteServices() {
      Iterable<RemoteService> remoteServices = env.datacenter.listRemoteServices();
      assertEquals(Iterables.size(remoteServices), env.remoteServices.size());

      remoteServices = env.datacenter.listRemoteServices(type(RemoteServiceType.NODE_COLLECTOR));
      assertEquals(Iterables.size(remoteServices), 1);
   }

   public void testFindRemoteService() {
      RemoteService remoteService = env.datacenter.findRemoteService(type(RemoteServiceType.NODE_COLLECTOR));
      assertNotNull(remoteService);
   }

}
