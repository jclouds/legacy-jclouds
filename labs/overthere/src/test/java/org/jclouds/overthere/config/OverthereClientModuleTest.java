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
package org.jclouds.overthere.config;

import org.easymock.EasyMock;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.overthere.OverthereRunner;
import org.jclouds.overthere.OverthereSshClient;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability to configure a {@link SshjSshClient}
 * 
 * @author Adrian Cole
 */
@Test
public class OverthereClientModuleTest {

   @Test
   public void testConfigureBindsSshClient() {
      Injector injector = Guice.createInjector(new OverthereSshClientModule(), new SLF4JLoggingModule());
      SshClient.Factory factory = injector.getInstance(SshClient.Factory.class);
      SshClient connection = factory.create(HostAndPort.fromParts("localhost", 22), LoginCredentials.builder().user("username")
            .password("password").build());
      assert connection instanceof OverthereSshClient;
   }
   
   @Test
   public void testConfigureBindsRunScriptOnNodeClient() {
      Injector injector = Guice.createInjector(new OverthereRunScriptClientModule(), new SLF4JLoggingModule());
      RunScriptOnNode.Factory factory = injector.getInstance(RunScriptOnNode.Factory.class);
      NodeMetadata node = EasyMock.createMock(NodeMetadata.class);
      Statement statement = Statements.exec("hostname");
      RunScriptOptions options = RunScriptOptions.Builder.wrapInInitScript(false);
      RunScriptOnNode runner = factory.create(node, statement, options);
      assert runner instanceof OverthereRunner;
   }
}
