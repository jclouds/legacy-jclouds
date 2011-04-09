/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code KeyPairClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class KeyPairClientLiveTest {

   private KeyPairClient client;
   private RestContext<EC2Client, EC2AsyncClient> context;

   protected String provider = "ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      endpoint = checkNotNull(System.getProperty("test." + provider + ".endpoint"), "test." + provider + ".endpoint");
      apiversion = checkNotNull(System.getProperty("test." + provider + ".apiversion"), "test." + provider
               + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      overrides.setProperty(provider + ".endpoint", endpoint);
      overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
      client = context.getApi().getKeyPairServices();
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
      assertNotNull(result.getKeyFingerprint());
      assertEquals(result.getKeyName(), keyName);

      Set<KeyPair> twoResults = Sets.newLinkedHashSet(client.describeKeyPairsInRegion(null, keyName));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      KeyPair listPair = twoResults.iterator().next();
      assertEquals(listPair.getKeyName(), result.getKeyName());
      assertEquals(listPair.getKeyFingerprint(), result.getKeyFingerprint());
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
