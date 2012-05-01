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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import com.google.inject.Module;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.compute.domain.PasswordDataAndPrivateKey;
import org.jclouds.ec2.compute.functions.WindowsLoginCredentialsFromEncryptedData;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.PasswordData;
import org.jclouds.ec2.reference.EC2Constants;
import org.jclouds.ec2.services.WindowsClient;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code WindowsClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "WindowsClientLiveTest")
public class WindowsClientLiveTest extends BaseComputeServiceContextLiveTest {
   public WindowsClientLiveTest() {
      provider = "aws-ec2";
   }

   private ComputeService computeService;
   private WindowsClient client;
   private static final String DEFAULT_INSTANCE = "i-TODO";
   private static final String DEFAULT_BUCKET = "TODO";
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getWindowsServices();
      computeService = view.getComputeService();
   }

   @Override
   protected Iterable<Module> setupModules() {
      Iterable<Module> superModules = super.setupModules();
      List<Module> modules = new ArrayList<Module>();
      Iterables.addAll(modules, superModules);
      modules.add(new BouncyCastleCryptoModule());
      return modules;
   }

   @Test(enabled = false)
   // TODO get instance
   public void testBundleInstanceInRegion() {
      client
               .bundleInstanceInRegion(
                        null,
                        DEFAULT_INSTANCE,
                        "prefix",
                        DEFAULT_BUCKET,
                        "{\"expiration\": \"2008-08-30T08:49:09Z\",\"conditions\": [{\"bucket\": \"my-bucket\"},[\"starts-with\", \"$key\", \"my-new-image\"]]}");
   }

   @Test(enabled = false)
   // TODO get instance
   public void testCancelBundleTaskInRegion() {

   }

   @Test(enabled = false)
   // TODO get instance
   public void testDescribeBundleTasksInRegion() {

   }

   @Test
   public void testGetPasswordDataInRegion() throws Exception {

      // Spin up a new node. Make sure to open the RDP port 3389
      Template template = computeService.templateBuilder()
         .osFamily(OsFamily.WINDOWS)
         .os64Bit(true)
         .imageNameMatches("Windows_Server-2008-R2_SP1-English-64Bit-Base-")
         .hardwareId(InstanceType.M1_LARGE)
         .options(TemplateOptions.Builder.inboundPorts(3389))
         .build();
      Set<? extends NodeMetadata> nodes = computeService.createNodesInGroup("test", 1, template);
      NodeMetadata node = Iterables.getOnlyElement(nodes);

      try {

         // The Administrator password will take some time before it is ready - Amazon says sometimes 15 minutes.
         // So we create a predicate that tests if the password is ready, and wrap it in a retryable predicate.
         Predicate<String> passwordReady = new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String s) {
               if (Strings.isNullOrEmpty(s)) return false;
               PasswordData data = client.getPasswordDataInRegion(null, s);
               if (data == null) return false;
               return !Strings.isNullOrEmpty(data.getPasswordData());
            }
         };
         RetryablePredicate<String> passwordReadyRetryable = new RetryablePredicate<String>(passwordReady, 600, 10, TimeUnit.SECONDS);
         assertTrue(passwordReadyRetryable.apply(node.getProviderId()));

         // Now pull together Amazon's encrypted password blob, and the private key that jclouds generated
         PasswordDataAndPrivateKey dataAndKey = new PasswordDataAndPrivateKey(
            client.getPasswordDataInRegion(null, node.getProviderId()),
            node.getCredentials().getPrivateKey());

         // And apply it to the decryption function
         WindowsLoginCredentialsFromEncryptedData f = view.getUtils().getInjector().getInstance(WindowsLoginCredentialsFromEncryptedData.class);
         LoginCredentials credentials = f.apply(dataAndKey);

         assertEquals(credentials.getUser(), "Administrator");
         assertFalse(Strings.isNullOrEmpty(credentials.getPassword()));
      } finally {
         computeService.destroyNode(node.getId());
      }
   }

}
