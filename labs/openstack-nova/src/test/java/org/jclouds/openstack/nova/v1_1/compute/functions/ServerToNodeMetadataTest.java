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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.UUID;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import org.easymock.EasyMock;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.openstack.nova.v1_1.compute.domain.RegionAndName;
import org.jclouds.openstack.nova.v1_1.domain.Address;
import org.jclouds.openstack.nova.v1_1.domain.Server;
import org.jclouds.openstack.nova.v1_1.domain.ServerStatus;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests for the function for transforming a nova specific Server into a generic
 * NodeMetadata object.
 * 
 * @author Matt Stephenson
 */
public class ServerToNodeMetadataTest {
   @Test
   public void testConversion() {
      UUID id = UUID.randomUUID();
      Server serverToConvert = Server.builder().id(id.toString()).name("Test Server " + id)
            .privateAddresses(Address.createV4("10.0.0.1")).publicAddresses(Address.createV4("1.0.1.1"))
            .status(ServerStatus.ACTIVE).metadata(ImmutableMap.of("test", "testing")).build();

      LoadingCache<RegionAndName, Iterable<String>> mockLoadingCache = EasyMock.createMock(LoadingCache.class);
      EasyMock.expect(mockLoadingCache.getUnchecked(new RegionAndName(null, id.toString()))).andReturn(ImmutableSet.<String>of());
      EasyMock.replay(mockLoadingCache);

      ServerToNodeMetadata converter = new ServerToNodeMetadata(mockLoadingCache);
      NodeMetadata convertedNodeMetadata = converter.apply(serverToConvert);

      assertEquals(serverToConvert.getId(), convertedNodeMetadata.getId());
      assertEquals(serverToConvert.getId(), convertedNodeMetadata.getProviderId());
      assertEquals(serverToConvert.getName(), convertedNodeMetadata.getName());
      assertEquals(serverToConvert.getStatus().getNodeState(), convertedNodeMetadata.getState());

      assertNotNull(convertedNodeMetadata.getPrivateAddresses());
      assertEquals(convertedNodeMetadata.getPrivateAddresses().size(), 1);
      assertEquals(convertedNodeMetadata.getPrivateAddresses().iterator().next(), "10.0.0.1");

      assertNotNull(convertedNodeMetadata.getPublicAddresses());
      assertEquals(convertedNodeMetadata.getPublicAddresses().size(), 1);
      assertEquals(convertedNodeMetadata.getPublicAddresses().iterator().next(), "1.0.1.1");

      assertNotNull(convertedNodeMetadata.getUserMetadata());
      assertEquals(convertedNodeMetadata.getUserMetadata().size(), 1);
      assertEquals(convertedNodeMetadata.getUserMetadata().get("test"), "testing");

      EasyMock.verify(mockLoadingCache);
   }   
}
