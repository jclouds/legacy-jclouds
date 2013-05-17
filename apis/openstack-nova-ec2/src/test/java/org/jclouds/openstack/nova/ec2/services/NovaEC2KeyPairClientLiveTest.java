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
package org.jclouds.openstack.nova.ec2.services;

import static com.google.common.collect.Sets.newTreeSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.openstack.nova.ec2.NovaEC2ApiMetadata;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code NovaKeyPairClient}
 * 
 * @author Adam Lowe
 */
@Test(groups = "live", singleThreaded = true)
public class NovaEC2KeyPairClientLiveTest extends BaseComputeServiceContextLiveTest {

   public static final String PREFIX = System.getProperty("user.name") + "-nova-ec2";

   public NovaEC2KeyPairClientLiveTest() {
      provider = "openstack-nova-ec2";
   }

   private NovaEC2KeyPairClient client;
   private Set<String> regions;
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(NovaEC2ApiMetadata.CONTEXT_TOKEN).getApi().getKeyPairServices();
      regions = view.unwrap(NovaEC2ApiMetadata.CONTEXT_TOKEN).getApi().getAvailabilityZoneAndRegionServices().describeRegions().keySet();
   }

   @Test
   void testDescribeKeyPairs() {
      for (String region : regions) {
         SortedSet<KeyPair> allResults = newTreeSet(client.describeKeyPairsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            KeyPair pair = allResults.last();
            SortedSet<KeyPair> result = newTreeSet(client.describeKeyPairsInRegion(region, pair.getKeyName()));
            assertNotNull(result);
            KeyPair compare = result.last();
            assertEquals(compare, pair);
         }
      }
   }

   @Test
   void testCreateKeyPair() {
      String keyName = PREFIX + "1";
      cleanupKeyPair(keyName);
      try {
         KeyPair keyPair = client.createKeyPairInRegion(null, keyName);
         checkKeyPair(keyName, keyPair);
         assertNotNull(keyPair.getKeyMaterial());
      } finally {
         cleanupKeyPair(keyName);
      }
   }

   protected void cleanupKeyPair(String keyName) {
      try {
         client.deleteKeyPairInRegion(null, keyName);
      } catch (Exception e) {

      }
      client.deleteKeyPairInRegion(null, keyName);
   }

   @Test
   void testImportKeyPair() throws IOException {
      String keyName = PREFIX + "2";
      cleanupKeyPair(keyName);
      Map<String, String> myKey = ComputeTestUtils.setupKeyPair();
      try {
         KeyPair keyPair = client.importKeyPairInRegion(null, keyName, myKey.get("public"));
         checkKeyPair(keyName, keyPair);

         // check the fingerprint of public key (in the sha10OfPrivateKey field)
         assertEquals(keyPair.getSha1OfPrivateKey(), SshKeys.fingerprintPublicKey(myKey.get("public")));

         // try again to see if there's an error
         try {
            client.importKeyPairInRegion(null, keyName, myKey.get("public"));
            fail("Duplicate call importKeyPairInRegion should have failed!");
         } catch (IllegalStateException e) {
         }
      } finally {
         cleanupKeyPair(keyName);
      }
   }

   protected void checkKeyPair(String keyName, KeyPair keyPair) {
      assertNotNull(keyPair);
      assertNotNull(keyPair.getSha1OfPrivateKey());
      assertEquals(keyPair.getKeyName(), keyName);

      Set<KeyPair> twoResults = client.describeKeyPairsInRegion(null, keyName);
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      KeyPair listPair = twoResults.iterator().next();
      assertEquals(listPair.getKeyName(), keyPair.getKeyName());
      assertEquals(listPair.getSha1OfPrivateKey(), keyPair.getSha1OfPrivateKey());
   }
}
