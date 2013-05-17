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
package org.jclouds.compute.callables;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.statements.login.UserAdd;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.eventbus.EventBus;

/**
 * @author Adam Lowe
 */
@Test(groups = { "unit" }, singleThreaded = true)
public class RunScriptOnNodeUsingSshTest {
   EventBus eventBus = new EventBus();

   private SshClient sshClient;
   private NodeMetadata node;
   private Function<NodeMetadata, SshClient> sshFactory;

   @BeforeMethod(groups = { "unit" })
   public void init() {
      sshClient = createMock(SshClient.class);
      sshFactory = new Function<NodeMetadata, SshClient>() {
         @Override
         public SshClient apply(@Nullable NodeMetadata nodeMetadata) {
            return sshClient;
         }
      };
      node = createMock(NodeMetadata.class);
      expect(node.getCredentials()).andReturn(LoginCredentials.builder().user("tester").password("notalot").build()).atLeastOnce();
      replay(node);
   }

   public void simpleTest() {
      RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, eventBus, node, exec("echo $USER\necho $USER"),
            wrapInInitScript(false).runAsRoot(false));

      testMe.init();

      sshClient.connect();
      expect(sshClient.getUsername()).andReturn("tester");
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
      expect(sshClient.exec("echo $USER\n" + "echo $USER\n")).andReturn(new ExecResponse("tester\ntester\n", null, 0));
      sshClient.disconnect();
      replay(sshClient);

      testMe.call();
   }

   public void simpleRootTest() {
      RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, eventBus, node, exec("echo $USER\necho $USER"),
            wrapInInitScript(false).runAsRoot(true));

      testMe.init();

      sshClient.connect();
      expect(sshClient.getUsername()).andReturn("tester");
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
      expect(
            sshClient.exec("sudo sh <<'RUN_SCRIPT_AS_ROOT_SSH'\n" + "echo $USER\n" + "echo $USER\n"
                  + "RUN_SCRIPT_AS_ROOT_SSH\n")).andReturn(new ExecResponse("root\nroot\n", null, 0));
      sshClient.disconnect();
      replay(sshClient);

      testMe.call();
   }

   public void simpleRootTestWithSudoPassword() {
      node = createMock(NodeMetadata.class);
      expect(node.getCredentials()).andReturn(LoginCredentials.builder().user("tester").password("testpassword!").authenticateSudo(true).build())
            .atLeastOnce();
      replay(node);
      RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, eventBus, node, exec("echo $USER\necho $USER"),
            wrapInInitScript(false).runAsRoot(true));
      testMe.init();

      sshClient.connect();
      expect(sshClient.getUsername()).andReturn("tester");
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
      expect(
            sshClient.exec("sudo -S sh <<'RUN_SCRIPT_AS_ROOT_SSH'\n" + "testpassword!\n" + "echo $USER\n"
                  + "echo $USER\n" + "RUN_SCRIPT_AS_ROOT_SSH\n")).andReturn(new ExecResponse("root\nroot\n", null, 0));
      sshClient.disconnect();
      replay(sshClient);

      testMe.call();
   }

   public void testUserAddAsRoot() {
      RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, eventBus, node, UserAdd.builder()
            .login("testuser").build(), wrapInInitScript(false).runAsRoot(true).overrideLoginPassword("test"));

      testMe.init();

      sshClient.connect();
      expect(sshClient.getUsername()).andReturn("tester");
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
      expect(
            sshClient.exec("sudo sh <<'RUN_SCRIPT_AS_ROOT_SSH'\n" + "mkdir -p /home/users\n"
                  + "useradd -c testuser -s /bin/bash -m  -d /home/users/testuser testuser\n"
                  + "chown -R testuser /home/users/testuser\n" + "RUN_SCRIPT_AS_ROOT_SSH\n")).andReturn(
            new ExecResponse("done", null, 0));
      sshClient.disconnect();
      replay(sshClient);

      testMe.call();
   }
}
