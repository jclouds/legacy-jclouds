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

package org.jclouds.virtualbox.compute;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.virtualbox.BaseVirtualBoxClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "VirtualBoxExperimentLiveTest")
public class VirtualBoxExperimentLiveTest extends BaseVirtualBoxClientLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   ComputeServiceContext context;

   @BeforeClass
   public void setUp() {
      context = ContextBuilder.newBuilder("virtualbox").modules(
               ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule())).build(
               ComputeServiceContext.class);
   }

   @Test
   public void testLaunchCluster() throws RunNodesException {
      int numNodes = 3;
      final String clusterName = "test-launch-cluster";
      Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup(clusterName, numNodes,
               TemplateOptions.Builder.overrideLoginUser("toor"));
      assertEquals(numNodes, nodes.size(), "wrong number of nodes");
      for (NodeMetadata node : nodes) {
         assertTrue(node.getGroup().equals("test-launch-cluster"));
         logger.debug("Created Node: %s", node);
         SshClient client = context.utils().sshForNode().apply(node);
         client.connect();
         ExecResponse hello = client.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
      }
      context.getComputeService().destroyNodesMatching(new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return input.getId().contains(clusterName);
         }
      });
   }

}