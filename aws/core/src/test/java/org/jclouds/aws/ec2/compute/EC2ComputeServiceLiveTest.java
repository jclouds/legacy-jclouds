/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.compute;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.domain.*;
import org.jclouds.domain.Credentials;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import java.util.Map;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.EC2ComputeServiceLiveTest")
public class EC2ComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "ec2";
   }

   @Test
   public void testTemplateBuilderCanUseImageId() {
      client.templateBuilder().imageId(Iterables.get(client.getImages().keySet(), 0)).build();
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assert (defaultTemplate.getImage().getId().startsWith("ami-")) : defaultTemplate;
      assertEquals(defaultTemplate.getImage().getName(), "9.10");
      assertEquals(defaultTemplate.getImage().getArchitecture(), Architecture.X86_32);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getId(), "us-east-1");
      assertEquals(defaultTemplate.getSize().getCores(), 1.0d);
      client.templateBuilder().osFamily(OsFamily.UBUNTU).smallest().architecture(
               Architecture.X86_32).imageId("ami-7e28ca17").build();
      client.templateBuilder().osFamily(OsFamily.UBUNTU).smallest().architecture(
               Architecture.X86_32).imageId("ami-bb709dd2").build();
   }

   @Test
   public void testScriptExecution() throws Exception {
      Template simpleTemplate = client.templateBuilder().smallest().build();
      client.runNodesWithTag("ec2", 1, simpleTemplate);
      Map<String, ? extends NodeMetadata> map = client.getNodesWithTag("ec2");
      map.values().iterator().next();
      Credentials creds = new Credentials("ubuntu", keyPair.get("public"));
      client.runScriptOnNodesWithTag("ec2", "mkdir ~/ahha; sleep 3".getBytes());
      client.destroyNodesWithTag("ec2");
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

}
