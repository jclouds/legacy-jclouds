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
package org.jclouds.gogrid;

import static org.jclouds.compute.domain.OsFamily.CENTOS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.jclouds.compute.BaseComputeServiceLiveTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * @author Oleksiy Yarmula
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "gogrid.GoGridComputeServiceLiveTest")
public class GoGridComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      service = "gogrid";
   }

   protected Template buildTemplate(TemplateBuilder templateBuilder) {
      return templateBuilder.osFamily(CENTOS).osDescriptionMatches(".*5.3.*").smallest().build();
   }

   @Override
   protected JschSshClientModule getSshModule() {
      return new JschSshClientModule();
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<GoGridAsyncClient, GoGridClient> tmContext = new ComputeServiceContextFactory()
               .createContext(service, user, password).getProviderSpecificContext();
   }

   @Test(enabled = false)
   public void endToEndComputeServiceTest() {
      ComputeService service = context.getComputeService();
      Template t = service.templateBuilder().minRam(1024).imageId(
               "GSI-6890f8b6-c8fb-4ac1-bc33-2563eb4e29d2").build();

      assertEquals(t.getImage().getId(), "GSI-6890f8b6-c8fb-4ac1-bc33-2563eb4e29d2");
      service.runNodesWithTag("testTag", 1, t);

      Map<String, ? extends ComputeMetadata> nodes = service.getNodes();
      assertEquals(nodes.size(), 1);

      NodeMetadata nodeMetadata = service.getNodeMetadata(Iterables.getOnlyElement(nodes.values()));
      assertEquals(nodeMetadata.getPublicAddresses().size(), 1,
               "There must be 1 public address for the node");
      assertTrue(nodeMetadata.getName().startsWith("testTag"));
      service.rebootNode(nodeMetadata); // blocks until finished

      assertEquals(service.getNodeMetadata(nodeMetadata).getState(), NodeState.RUNNING);
      service.destroyNode(nodeMetadata);
   }
}
