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
package org.jclouds.ec2.compute.strategy;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.features.ElasticIPAddressApi;
import org.jclouds.ec2.features.InstanceApi;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "EC2DestroyNodeStrategyTest")
public class EC2DestroyNodeStrategyTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testDestroyNodeTerminatesInstanceAndReturnsRefreshedNode() throws Exception {
      EC2Api client = createMock(EC2Api.class);
      InstanceApi instanceClient = createMock(InstanceApi.class);
      GetNodeMetadataStrategy getNode = createMock(GetNodeMetadataStrategy.class);
      LoadingCache<RegionAndName, String> elasticIpCache = createMock(LoadingCache.class);

      NodeMetadata node = createMock(NodeMetadata.class);

      expect(client.getInstanceApi()).andReturn((Optional) Optional.of(instanceClient)).atLeastOnce();
      expect(instanceClient.terminateInstancesInRegion("region", "i-blah")).andReturn(null);
      expect(getNode.getNode("region/i-blah")).andReturn(node);

      replay(client);
      replay(getNode);
      replay(instanceClient);
      replay(elasticIpCache);

      EC2DestroyNodeStrategy destroyer = new EC2DestroyNodeStrategy(client, getNode, elasticIpCache);

      assertEquals(destroyer.destroyNode("region/i-blah"), node);

      verify(client);
      verify(getNode);
      verify(instanceClient);
      verify(elasticIpCache);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testDestroyNodeDisassociatesAndReleasesIpThenTerminatesInstanceAndReturnsRefreshedNode()
            throws Exception {
      EC2Api client = createMock(EC2Api.class);
      GetNodeMetadataStrategy getNode = createMock(GetNodeMetadataStrategy.class);
      LoadingCache<RegionAndName, String> elasticIpCache = createMock(LoadingCache.class);
      ElasticIPAddressApi ipClient = createMock(ElasticIPAddressApi.class);
      InstanceApi instanceClient = createMock(InstanceApi.class);

      NodeMetadata node = createMock(NodeMetadata.class);

      expect(elasticIpCache.get(new RegionAndName("region", "i-blah"))).andReturn("1.1.1.1");

      expect(client.getElasticIPAddressApi()).andReturn((Optional) Optional.of(ipClient)).atLeastOnce();
      ipClient.disassociateAddressInRegion("region", "1.1.1.1");
      ipClient.releaseAddressInRegion("region", "1.1.1.1");
      elasticIpCache.invalidate(new RegionAndName("region", "i-blah"));


      expect(client.getInstanceApi()).andReturn((Optional) Optional.of(instanceClient)).atLeastOnce();
      expect(instanceClient.terminateInstancesInRegion("region", "i-blah")).andReturn(null);
      expect(getNode.getNode("region/i-blah")).andReturn(node);

      replay(client);
      replay(getNode);
      replay(elasticIpCache);
      replay(instanceClient);
      replay(ipClient);

      EC2DestroyNodeStrategy destroyer = new EC2DestroyNodeStrategy(client, getNode, elasticIpCache);
      destroyer.autoAllocateElasticIps = true;

      assertEquals(destroyer.destroyNode("region/i-blah"), node);

      verify(client);
      verify(getNode);
      verify(elasticIpCache);
      verify(instanceClient);
      verify(ipClient);
   }
   

   @SuppressWarnings("unchecked")
   @Test
   public void testDestroyNodeSafeOnCacheMissThenTerminatesInstanceAndReturnsRefreshedNode()
            throws Exception {
      EC2Api client = createMock(EC2Api.class);
      GetNodeMetadataStrategy getNode = createMock(GetNodeMetadataStrategy.class);
      LoadingCache<RegionAndName, String> elasticIpCache = createMock(LoadingCache.class);
      ElasticIPAddressApi ipClient = createMock(ElasticIPAddressApi.class);
      InstanceApi instanceClient = createMock(InstanceApi.class);

      NodeMetadata node = createMock(NodeMetadata.class);

      expect(elasticIpCache.get(new RegionAndName("region", "i-blah"))).andThrow(new CacheLoader.InvalidCacheLoadException(null));

      expect(client.getInstanceApi()).andReturn((Optional) Optional.of(instanceClient)).atLeastOnce();
      expect(instanceClient.terminateInstancesInRegion("region", "i-blah")).andReturn(null);
      expect(getNode.getNode("region/i-blah")).andReturn(node);

      replay(client);
      replay(getNode);
      replay(elasticIpCache);
      replay(instanceClient);
      replay(ipClient);

      EC2DestroyNodeStrategy destroyer = new EC2DestroyNodeStrategy(client, getNode, elasticIpCache);
      destroyer.autoAllocateElasticIps = true;

      assertEquals(destroyer.destroyNode("region/i-blah"), node);

      verify(client);
      verify(getNode);
      verify(elasticIpCache);
      verify(instanceClient);
      verify(ipClient);
   }
   

   @SuppressWarnings("unchecked")
   @Test
   public void testDestroyNodeSafeOnCacheExecutionExceptionThenTerminatesInstanceAndReturnsRefreshedNode()
            throws Exception {
      EC2Api client = createMock(EC2Api.class);
      GetNodeMetadataStrategy getNode = createMock(GetNodeMetadataStrategy.class);
      LoadingCache<RegionAndName, String> elasticIpCache = createMock(LoadingCache.class);
      ElasticIPAddressApi ipClient = createMock(ElasticIPAddressApi.class);
      InstanceApi instanceClient = createMock(InstanceApi.class);

      NodeMetadata node = createMock(NodeMetadata.class);

      expect(elasticIpCache.get(new RegionAndName("region", "i-blah"))).andThrow(new ExecutionException(null));

      expect(client.getInstanceApi()).andReturn((Optional) Optional.of(instanceClient)).atLeastOnce();
      expect(instanceClient.terminateInstancesInRegion("region", "i-blah")).andReturn(null);
      expect(getNode.getNode("region/i-blah")).andReturn(node);

      replay(client);
      replay(getNode);
      replay(elasticIpCache);
      replay(instanceClient);
      replay(ipClient);

      EC2DestroyNodeStrategy destroyer = new EC2DestroyNodeStrategy(client, getNode, elasticIpCache);
      destroyer.autoAllocateElasticIps = true;

      assertEquals(destroyer.destroyNode("region/i-blah"), node);

      verify(client);
      verify(getNode);
      verify(elasticIpCache);
      verify(instanceClient);
      verify(ipClient);
   }
}
