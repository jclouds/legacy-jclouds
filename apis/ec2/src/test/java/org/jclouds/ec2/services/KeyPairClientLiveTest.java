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
package org.jclouds.ec2.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.domain.Region;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.domain.KeyPair;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code KeyPairClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "KeyPairClientLiveTest")
public class KeyPairClientLiveTest extends BaseComputeServiceContextLiveTest {
   public KeyPairClientLiveTest() {
      provider = "ec2";
   }

   private KeyPairClient client;
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getKeyPairServices();
   }

   @Test
   void testDescribeKeyPairs() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
               Region.AP_SOUTHEAST_1)) {

         SortedSet<KeyPair> allResults = Sets.newTreeSet(client.describeKeyPairsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            KeyPair pair = allResults.last();
            SortedSet<KeyPair> result = Sets.newTreeSet(client.describeKeyPairsInRegion(region, pair.getKeyName()));
            assertNotNull(result);
            KeyPair compare = result.last();
            assertEquals(compare, pair);
         }
      }
   }

   public static final String PREFIX = System.getProperty("user.name") + "-ec2";

   @Test
   void testCreateKeyPair() {
      String keyName = PREFIX + "1";
      try {
         client.deleteKeyPairInRegion(null, keyName);
      } catch (Exception e) {

      }
      client.deleteKeyPairInRegion(null, keyName);

      KeyPair result = client.createKeyPairInRegion(null, keyName);
      assertNotNull(result);
      assertNotNull(result.getKeyMaterial());
      assertNotNull(result.getSha1OfPrivateKey());
      assertEquals(result.getKeyName(), keyName);

      Set<KeyPair> twoResults = Sets.newLinkedHashSet(client.describeKeyPairsInRegion(null, keyName));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      KeyPair listPair = twoResults.iterator().next();
      assertEquals(listPair.getKeyName(), result.getKeyName());
      assertEquals(listPair.getSha1OfPrivateKey(), result.getSha1OfPrivateKey());
   }

}
