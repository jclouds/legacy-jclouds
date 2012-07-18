/*
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
package org.jclouds.nodepool;

import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.nodepool.config.NodePoolProperties.BACKEND_PROVIDER;
import static org.jclouds.nodepool.config.NodePoolProperties.BASEDIR;
import static org.jclouds.nodepool.config.NodePoolProperties.MAX_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.MIN_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.REMOVE_DESTROYED;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole, David Alves
 */
@Test(groups = "live", testName = "BYONBackendLiveTest")
public class BYONBackendLiveTest extends BaseComputeServiceContextLiveTest {

   final String basedir = "target/" + this.getClass().getSimpleName();

   public BYONBackendLiveTest() {
      provider = "nodepool";
   }

   @Override
   protected Properties setupProperties() {

      Properties contextProperties = super.setupProperties();

      contextProperties.setProperty(BACKEND_PROVIDER, "byon");
      contextProperties.setProperty(BASEDIR, basedir);
      contextProperties.setProperty(MAX_SIZE, 1 + "");
      contextProperties.setProperty(MIN_SIZE, 1 + "");
      contextProperties.setProperty(REMOVE_DESTROYED, false + "");
      contextProperties.setProperty("nodepool.identity", System.getProperty("user.name"));

      StringBuilder nodes = new StringBuilder();
      nodes.append("nodes:\n");
      nodes.append("    - id: mymachine\n");
      nodes.append("      location_id: localhost\n");
      nodes.append("      name: my local machine\n");
      nodes.append("      hostname: localhost\n");
      nodes.append("      os_arch: ").append(System.getProperty("os.arch")).append("\n");
      nodes.append("      os_family: ").append(OsFamily.UNIX).append("\n");
      nodes.append("      os_description: ").append(System.getProperty("os.name")).append("\n");
      nodes.append("      os_version: ").append(System.getProperty("os.version")).append("\n");
      nodes.append("      group: ").append("nodepool").append("\n");
      nodes.append("      tags:\n");
      nodes.append("          - local\n");
      nodes.append("      username: ").append(System.getProperty("user.name")).append("\n");
      nodes.append("      credential_url: file://").append(System.getProperty("user.home")).append("/.ssh/id_rsa")
               .append("\n");

      contextProperties.setProperty("byon.nodes", nodes.toString());
      contextProperties.setProperty("byon.template", "locationId=localhost");
      return contextProperties;
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Test(groups = "live")
   public void testCanRunCommandAsCurrentUser() throws Exception {
      Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup("goo", 1);
      NodeMetadata node = Iterables.get(nodes, 0);

      try {
         ExecResponse response = view.getComputeService().runScriptOnNode(node.getId(), exec("id"),
                  wrapInInitScript(false).runAsRoot(false));
         assert response.getOutput().trim().contains(System.getProperty("user.name")) : node + ": " + response;
      } finally {
         view.getComputeService().destroyNode(node.getId());
      }
   }

   @Override
   protected void tearDownContext() {
      super.tearDownContext();
      new File(basedir).delete();
   }
}
