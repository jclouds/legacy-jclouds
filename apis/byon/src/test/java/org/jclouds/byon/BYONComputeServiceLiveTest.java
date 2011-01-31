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

package org.jclouds.byon;

import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BYONComputeServiceLiveTest {

   private ComputeServiceContext context;

   @BeforeClass(groups = "live")
   public void setup() throws FileNotFoundException, IOException {
      Properties contextProperties = new Properties();

      StringBuilder nodes = new StringBuilder();
      nodes.append("nodes:\n");
      nodes.append("    - id: mymachine\n");
      nodes.append("      name: my local machine\n");
      nodes.append("      hostname: localhost\n");
      nodes.append("      os_arch: ").append(System.getProperty("os.arch")).append("\n");
      nodes.append("      os_family: ").append(OsFamily.UNIX).append("\n");
      nodes.append("      os_description: ").append(System.getProperty("os.name")).append("\n");
      nodes.append("      os_version: ").append(System.getProperty("os.version")).append("\n");
      nodes.append("      group: ").append("ssh").append("\n");
      nodes.append("      tags:\n");
      nodes.append("          - local\n");
      nodes.append("      username: ").append(System.getProperty("user.name")).append("\n");
      nodes.append("      credential_url: file://").append(System.getProperty("user.home")).append("/.ssh/id_rsa")
               .append("\n");

      contextProperties.setProperty("byon.nodes", nodes.toString());

      context = new ComputeServiceContextFactory().createContext("byon", "foo", "bar", ImmutableSet.<Module> of(
               new JschSshClientModule(), new Log4JLoggingModule()), contextProperties);
   }

   public void testCanRunCommandAsCurrentUser() throws Exception {
      Map<? extends NodeMetadata, ExecResponse> responses = context.getComputeService().runScriptOnNodesMatching(
               Predicates.<NodeMetadata> alwaysTrue(), exec("id"), wrapInInitScript(false).runAsRoot(false));

      for (Entry<? extends NodeMetadata, ExecResponse> response : responses.entrySet())
         assert response.getValue().getOutput().trim().contains(System.getProperty("user.name")) : response.getKey()
                  + ": " + response.getValue();
   }

   @AfterClass(groups = "live")
   public void close() throws FileNotFoundException, IOException {
      if (context != null)
         context.close();
   }
}
