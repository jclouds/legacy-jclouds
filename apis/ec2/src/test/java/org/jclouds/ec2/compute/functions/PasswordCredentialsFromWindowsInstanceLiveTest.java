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
package org.jclouds.ec2.compute.functions;

import static org.jclouds.compute.options.TemplateOptions.Builder.inboundPorts;
import static org.jclouds.ec2.reference.EC2Constants.PROPERTY_EC2_AMI_OWNERS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

import java.util.Properties;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.testng.annotations.Test;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Tests behavior of {@code WindowsApi}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "PasswordCredentialsFromWindowsInstanceLiveTest")
public class PasswordCredentialsFromWindowsInstanceLiveTest extends BaseComputeServiceContextLiveTest {
   protected TemplateBuilderSpec windowsTemplate;

   public PasswordCredentialsFromWindowsInstanceLiveTest() {
      provider = "ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      String windowsSpec = setIfTestSystemPropertyPresent(overrides, provider + ".windows-template");
      if (Strings.emptyToNull(windowsSpec) != null) {
         windowsTemplate = TemplateBuilderSpec.parse(windowsSpec);
      }
      String windowsOwner = setIfTestSystemPropertyPresent(overrides, provider + ".windows-owner");
      if (Strings.emptyToNull(windowsOwner) != null) {
         overrides.setProperty(PROPERTY_EC2_AMI_OWNERS, windowsOwner);
      }
      return overrides;
   }

   // TODO: refactor so that we don't need to use bouncycastle
   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> builder().addAll(super.setupModules()).add(new BouncyCastleCryptoModule()).build();
   }

   @Test
   public void testWindowsAdminWorks() throws Exception {
      String group = "winadm";
      // Spin up a new node. Make sure to open the RDP port 3389
      Template template = view.getComputeService().templateBuilder().from(windowsTemplate).options(inboundPorts(3389))
               .build();
      try {
         NodeMetadata node = Iterables.getOnlyElement(view.getComputeService().createNodesInGroup(group, 1, template));
         assertEquals(node.getCredentials().getUser(), "Administrator");
         assertFalse(Strings.isNullOrEmpty(node.getCredentials().getPassword()));
      } finally {
         view.getComputeService().destroyNodesMatching(NodePredicates.inGroup(group));
      }
   }

}
