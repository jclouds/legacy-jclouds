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
package org.jclouds.ec2.compute.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.testng.annotations.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "AddElasticIpsToNodemetadataTest")
public class AddElasticIpsToNodemetadataTest {

   NodeMetadata node = new NodeMetadataBuilder().status(Status.RUNNING).group("zkclustertest").name("foo").hostname(
            "ip-10-212-81-7").privateAddresses(ImmutableSet.of("10.212.81.7")).publicAddresses(
            ImmutableSet.of("174.129.173.155")).imageId("us-east-1/ami-63be790a").id("us-east-1/i-911444f0")
            .providerId("i-911444f0").tags(ImmutableSet.of("Empty")).userMetadata(ImmutableMap.of("Name", "foo"))
            .build();

   @Test
   public void testReturnsNodeWithElasticIpWhenFoundInCacheAndNodeHadAPublicIp() throws Exception {
      RegionAndName key = new RegionAndName("us-east-1", node.getProviderId());
      String val = "1.1.1.1";
      LoadingCache<RegionAndName, String> cache = cacheOf(key, val);

      AddElasticIpsToNodemetadata fn = new AddElasticIpsToNodemetadata(cache);

      assertEquals(fn.apply(node).getPublicAddresses(), ImmutableSet.of("1.1.1.1"));
   }

   @Test
   public void testReturnsNodeWithIpWhenFoundInCacheAndNodeHadNoPublicIp() throws Exception {
      RegionAndName key = new RegionAndName("us-east-1", node.getProviderId());
      String val = "1.1.1.1";
      LoadingCache<RegionAndName, String> cache = cacheOf(key, val);

      AddElasticIpsToNodemetadata fn = new AddElasticIpsToNodemetadata(cache);

      assertEquals(fn.apply(
               NodeMetadataBuilder.fromNodeMetadata(node).publicAddresses(ImmutableSet.<String> of()).build())
               .getPublicAddresses(), ImmutableSet.of("1.1.1.1"));
   }

   @Test
   public void testReturnsSameNodeWhenNotFoundInCache() throws Exception {
      RegionAndName key = new RegionAndName("us-east-1", node.getProviderId());
      String val = null;
      LoadingCache<RegionAndName, String> cache = cacheOf(key, val);

      AddElasticIpsToNodemetadata fn = new AddElasticIpsToNodemetadata(cache);

      assertEquals(fn.apply(node).getPublicAddresses(), ImmutableSet.of("174.129.173.155"));
   }

   private LoadingCache<RegionAndName, String> cacheOf(final RegionAndName key, final String val) {
      return CacheBuilder.newBuilder().build(new CacheLoader<RegionAndName, String>() {

         @Override
         public String load(RegionAndName in) throws Exception {
            return key.equals(in) ? val : null;
         }

      });
   }
}
