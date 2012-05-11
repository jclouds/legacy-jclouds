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
package org.jclouds.aws.ec2.services;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Sets.newTreeSet;
import static org.jclouds.compute.options.TemplateOptions.Builder.overrideLoginCredentials;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeTestUtils;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AWSKeyPairClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class AWSKeyPairClientLiveTest extends BaseComputeServiceContextLiveTest {
   public AWSKeyPairClientLiveTest() {
      provider = "aws-ec2";
   }

   private AWSKeyPairClient client;
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi().getKeyPairServices();
   }

   public void testNoSsh() throws Exception {

      Map<String, String> keyPair = ComputeTestUtils.setupKeyPair();

      AWSInstanceClient instanceClient = AWSEC2Client.class.cast(view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi()).getInstanceServices();

      String group = PREFIX + "unssh";
      view.getComputeService().destroyNodesMatching(inGroup(group));

      TemplateOptions options = view.getComputeService().templateOptions();

      options.authorizePublicKey(keyPair.get("public")).as(AWSEC2TemplateOptions.class);

      ComputeServiceContext noSshContext = null;
      try {
         noSshContext = createView(setupProperties(), ImmutableSet.<Module> of(new Log4JLoggingModule()));

         Set<? extends NodeMetadata> nodes = noSshContext.getComputeService().createNodesInGroup(group, 1, options);

         NodeMetadata first = get(nodes, 0);
         assert first.getCredentials() != null : first;
         assert first.getCredentials().identity != null : first;
         // credentials should not be present as the import public key call doesn't have access to
         // the related private key
         assert first.getCredentials().credential == null : first;

         AWSRunningInstance instance = getInstance(instanceClient, first.getProviderId());

         assertEquals(instance.getKeyName(), "jclouds#" + group);

         Map<? extends NodeMetadata, ExecResponse> responses = view.getComputeService()
               .runScriptOnNodesMatching(
                     runningInGroup(group),
                     exec("echo hello"),
                     overrideLoginCredentials(
                           LoginCredentials.builder().user(first.getCredentials().identity)
                                 .privateKey(keyPair.get("private")).build()).wrapInInitScript(false).runAsRoot(false));

         ExecResponse hello = getOnlyElement(responses.values());
         assertEquals(hello.getOutput().trim(), "hello");

      } finally {
         noSshContext.close();
         view.getComputeService().destroyNodesMatching(inGroup(group));
      }
   }

   @Test
   void testDescribeAWSKeyPairs() {
      for (String region : Region.DEFAULT_REGIONS) {

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
      assertNotNull(keyPair.getSha1OfPrivateKey());
      assertEquals(keyPair.getKeyName(), keyName);

      Set<KeyPair> twoResults = client.describeKeyPairsInRegion(null, keyName);
      assertNotNull(twoResults);
      assertEquals(twoResults.size(), 1);
      KeyPair listPair = twoResults.iterator().next();
      assertEquals(listPair.getKeyName(), keyPair.getKeyName());
      assertEquals(listPair.getSha1OfPrivateKey(), keyPair.getSha1OfPrivateKey());
   }

   protected AWSRunningInstance getInstance(AWSInstanceClient instanceClient, String id) {
      return getOnlyElement(getOnlyElement(instanceClient.describeInstancesInRegion(null, id)));
   }
   
   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
