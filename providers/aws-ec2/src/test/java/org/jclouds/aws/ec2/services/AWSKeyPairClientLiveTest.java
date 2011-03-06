/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2AsyncClient;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AWSKeyPairClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class AWSKeyPairClientLiveTest {

   private AWSKeyPairClient client;
   private RestContext<AWSEC2Client, AWSEC2AsyncClient> context;

   protected String provider = "aws-ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint", null);
      apiversion = System.getProperty("test." + provider + ".apiversion", null);
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
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
   void testDescribeAWSKeyPairs() {
      for (String region : Region.DEFAULT_REGIONS) {

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
   void testImportKeyPair() throws FileNotFoundException, IOException {
      String keyName = PREFIX + "2";
      cleanupKeyPair(keyName);
      Map<String, String> myKey = ComputeTestUtils.setupKeyPair();
      try {
         KeyPair keyPair = client.importKeyPairInRegion(null, keyName, myKey.get("public"));
         checkKeyPair(keyName, keyPair);
         // TODO generate correct fingerprint and check
         // assertEquals(keyPair.getKeyFingerprint(),
         // CryptoStreams.hex(CryptoStreams.md5(myKey.get("public").getBytes())));

         // try again to see if there's an error
         try {
            client.importKeyPairInRegion(null, keyName, myKey.get("public"));
            assert false;
         } catch (IllegalStateException e) {

         }
      } finally {
         cleanupKeyPair(keyName);
      }
   }

   protected void checkKeyPair(String keyName, KeyPair keyPair) {
      assertNotNull(keyPair);
      assertNotNull(keyPair.getKeyFingerprint());
      assertEquals(keyPair.getKeyName(), keyName);

      Set<KeyPair> twoResults = Sets.newLinkedHashSet(client.describeKeyPairsInRegion(null, keyName));
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      KeyPair listPair = twoResults.iterator().next();
      assertEquals(listPair.getKeyName(), keyPair.getKeyName());
      assertEquals(listPair.getKeyFingerprint(), keyPair.getKeyFingerprint());
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
