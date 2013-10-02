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

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;
import static com.google.inject.name.Names.named;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilCompleteTest")
public class RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilCompleteTest {
   Injector injector = Guice.createInjector(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()),
         new AbstractModule() {
            protected void configure() {
               bindConstant().annotatedWith(named(PROPERTY_USER_THREADS)).to(1);
               bindConstant().annotatedWith(named(PROPERTY_IO_WORKER_THREADS)).to(1);
               bindConstant().annotatedWith(named(TIMEOUT_SCRIPT_COMPLETE)).to(100);
               install(new FactoryModuleBuilder().build(BlockUntilInitScriptStatusIsZeroThenReturnOutput.Factory.class));
            }
         });
   EventBus eventBus = new EventBus();
   BlockUntilInitScriptStatusIsZeroThenReturnOutput.Factory statusFactory = injector
         .getInstance(BlockUntilInitScriptStatusIsZeroThenReturnOutput.Factory.class);

   // fail faster than normal
   Timeouts timeouts = injector.getInstance(Timeouts.class);

   @Test(expectedExceptions = IllegalStateException.class)
   public void testWithoutInitThrowsIllegalStateException() {
      Statement command = exec("doFoo");
      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).credentials(
            LoginCredentials.builder().user("tester").password("testpassword!").build()).build();

      SshClient sshClient = createMock(SshClient.class);

      replay(sshClient);

      RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete testMe = new RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
               statusFactory, timeouts, Functions.forMap(ImmutableMap.of(node, sshClient)),
               eventBus, InitScriptConfigurationForTasks.create().appendIncrementingNumberToAnonymousTaskNames(), node, command,
               new RunScriptOptions());

      testMe.call();
   }

   public void testDefault() {
      Statement command = exec("doFoo");
      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING)
            .credentials(LoginCredentials.builder().user("tester").password("testpassword!").build()).build();

      SshClient sshClient = createMock(SshClient.class);

      InitScript init = InitScript.builder().name("jclouds-script-0").home("/tmp/jclouds-script-0").run(command)
            .build();

      sshClient.connect();
      sshClient.put("/tmp/init-jclouds-script-0", init.render(OsFamily.UNIX));
      expect(sshClient.getUsername()).andReturn("tester").atLeastOnce();
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com").atLeastOnce();

      // setup script as default user
      expect(sshClient.exec("chmod 755 /tmp/init-jclouds-script-0")).andReturn(new ExecResponse("", "", 0));
      expect(sshClient.exec("ln -fs /tmp/init-jclouds-script-0 jclouds-script-0")).andReturn(
               new ExecResponse("", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 init")).andReturn(new ExecResponse("", "", 0));

      // start script as root via sudo, note that since there's no adminPassword we do a straight
      // sudo
      expect(sshClient.exec("sudo /tmp/init-jclouds-script-0 start")).andReturn(new ExecResponse("", "", 0));

      // signal the command completed
      expect(sshClient.exec("/tmp/init-jclouds-script-0 status")).andReturn(new ExecResponse("", "", 1)).times(1);
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stdout")).andReturn(new ExecResponse("out", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stderr")).andReturn(new ExecResponse("err", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 exitstatus")).andReturn(new ExecResponse("0", "", 0));

      sshClient.disconnect();
      replay(sshClient);

      RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete testMe = new RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
               statusFactory, timeouts, Functions.forMap(ImmutableMap.of(node, sshClient)),
               eventBus, InitScriptConfigurationForTasks.create().appendIncrementingNumberToAnonymousTaskNames(), node, command,
               new RunScriptOptions());

      assertEquals(testMe.getInitFile(), "/tmp/init-jclouds-script-0");
      assertEquals(testMe.getNode(), node);
      assertEquals(testMe.getStatement(), init);

      testMe.init();
      
      assertEquals(testMe.call(), new ExecResponse("out", "err", 0));

      verify(sshClient);
   }

   public void testWithSudoPassword() {
      Statement command = exec("doFoo");
      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).credentials(
            LoginCredentials.builder().user("tester").password("testpassword!").authenticateSudo(true).build()).build();

      SshClient sshClient = createMock(SshClient.class);

      InitScript init = InitScript.builder().name("jclouds-script-0").home("/tmp/jclouds-script-0").run(command)
            .build();

      sshClient.connect();
      sshClient.put("/tmp/init-jclouds-script-0", init.render(OsFamily.UNIX));
      expect(sshClient.getUsername()).andReturn("tester").atLeastOnce();
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com").atLeastOnce();

      // setup script as default user
      expect(sshClient.exec("chmod 755 /tmp/init-jclouds-script-0")).andReturn(new ExecResponse("", "", 0));
      expect(sshClient.exec("ln -fs /tmp/init-jclouds-script-0 jclouds-script-0")).andReturn(
               new ExecResponse("", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 init")).andReturn(new ExecResponse("", "", 0));

      // since there's an adminPassword we must pass this in
      expect(sshClient.exec("echo 'testpassword!'|sudo -S /tmp/init-jclouds-script-0 start")).andReturn(new ExecResponse("", "", 0));

      // signal the command completed
      expect(sshClient.exec("/tmp/init-jclouds-script-0 status")).andReturn(new ExecResponse("", "", 1));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stdout")).andReturn(new ExecResponse("out", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stderr")).andReturn(new ExecResponse("err", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 exitstatus")).andReturn(new ExecResponse("0", "", 0));

      sshClient.disconnect();
      replay(sshClient);

      RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete testMe = new RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
               statusFactory, timeouts, Functions.forMap(ImmutableMap.of(node, sshClient)),
               eventBus, InitScriptConfigurationForTasks.create().appendIncrementingNumberToAnonymousTaskNames(), node, command,
               new RunScriptOptions());

      assertEquals(testMe.getInitFile(), "/tmp/init-jclouds-script-0");
      assertEquals(testMe.getNode(), node);
      assertEquals(testMe.getStatement(), init);

      testMe.init();
      
      assertEquals(testMe.call(), new ExecResponse("out", "err", 0));
      
      verify(sshClient);
   }

   /**
    * in a couple versions of ubuntu on aws-ec2, status returneds no pid (ex. empty stdout w/exit code 1) transiently. sadly, we need to doublecheck status before assuming it has failed.
    * 
    */
   public void testDoublecheckStatusInCaseTransientlyWrong() {
      Statement command = exec("doFoo");
      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).credentials(
            LoginCredentials.builder().user("tester").password("testpassword!").authenticateSudo(true).build()).build();

      SshClient sshClient = createMock(SshClient.class);

      InitScript init = InitScript.builder().name("jclouds-script-0").home("/tmp/jclouds-script-0").run(command)
            .build();

      sshClient.connect();
      sshClient.put("/tmp/init-jclouds-script-0", init.render(OsFamily.UNIX));
      expect(sshClient.getUsername()).andReturn("tester").atLeastOnce();
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com").atLeastOnce();

      // setup script as default user
      expect(sshClient.exec("chmod 755 /tmp/init-jclouds-script-0")).andReturn(new ExecResponse("", "", 0));
      expect(sshClient.exec("ln -fs /tmp/init-jclouds-script-0 jclouds-script-0")).andReturn(
               new ExecResponse("", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 init")).andReturn(new ExecResponse("", "", 0));

      // since there's an adminPassword we must pass this in
      expect(sshClient.exec("echo 'testpassword!'|sudo -S /tmp/init-jclouds-script-0 start")).andReturn(new ExecResponse("", "", 0));

      // signal the command completed
      expect(sshClient.exec("/tmp/init-jclouds-script-0 status")).andReturn(new ExecResponse("8001", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 status")).andReturn(new ExecResponse("", "", 1));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stdout")).andReturn(new ExecResponse("out", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stderr")).andReturn(new ExecResponse("err", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 exitstatus")).andReturn(new ExecResponse("0", "", 0));

      sshClient.disconnect();
      replay(sshClient);

      RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete testMe = new RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
               statusFactory, timeouts, Functions.forMap(ImmutableMap.of(node, sshClient)),
               eventBus, InitScriptConfigurationForTasks.create().appendIncrementingNumberToAnonymousTaskNames(), node, command,
               new RunScriptOptions());

      assertEquals(testMe.getInitFile(), "/tmp/init-jclouds-script-0");
      assertEquals(testMe.getNode(), node);
      assertEquals(testMe.getStatement(), init);

      testMe.init();
      
      assertEquals(testMe.call(), new ExecResponse("out", "err", 0));
      
      verify(sshClient);
   }
   
   public void testNotRoot() {
      Statement command = exec("doFoo");
      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).credentials(
            LoginCredentials.builder().user("tester").password("testpassword!").authenticateSudo(true).build()).build();

      SshClient sshClient = createMock(SshClient.class);

      InitScript init = InitScript.builder().name("jclouds-script-0").home("/tmp/jclouds-script-0").run(command)
            .build();

      sshClient.connect();
      sshClient.put("/tmp/init-jclouds-script-0", init.render(OsFamily.UNIX));
      expect(sshClient.getUsername()).andReturn("tester").atLeastOnce();
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com").atLeastOnce();

      // setup script as default user
      expect(sshClient.exec("chmod 755 /tmp/init-jclouds-script-0")).andReturn(new ExecResponse("", "", 0));
      expect(sshClient.exec("ln -fs /tmp/init-jclouds-script-0 jclouds-script-0")).andReturn(
               new ExecResponse("", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 init")).andReturn(new ExecResponse("", "", 0));

      // kick off as current user
      expect(sshClient.exec("/tmp/init-jclouds-script-0 start")).andReturn(new ExecResponse("", "", 0));

      // signal the command completed
      expect(sshClient.exec("/tmp/init-jclouds-script-0 status")).andReturn(new ExecResponse("", "", 1));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stdout")).andReturn(new ExecResponse("out", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stderr")).andReturn(new ExecResponse("err", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 exitstatus")).andReturn(new ExecResponse("0", "", 0));

      sshClient.disconnect();
      replay(sshClient);

      RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete testMe = new RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
               statusFactory, timeouts, Functions.forMap(ImmutableMap.of(node, sshClient)),
               eventBus, InitScriptConfigurationForTasks.create().appendIncrementingNumberToAnonymousTaskNames(), node, command,
               new RunScriptOptions().runAsRoot(false));

      assertEquals(testMe.getInitFile(), "/tmp/init-jclouds-script-0");
      assertEquals(testMe.getNode(), node);
      assertEquals(testMe.getStatement(), init);

      testMe.init();
      
      assertEquals(testMe.call(), new ExecResponse("out", "err", 0));

      verify(sshClient);
   }
   
   public void testBadReturnCode() {
      Statement command = exec("doFoo");
      NodeMetadata node = new NodeMetadataBuilder().ids("badreturncode").status(Status.RUNNING).credentials(
            LoginCredentials.builder().user("tester").password("testpassword!").authenticateSudo(true).build()).build();
      
      SshClient sshClient = createMock(SshClient.class);

      InitScript init = InitScript.builder().name("jclouds-script-0").home("/tmp/jclouds-script-0").run(command)
            .build();

      sshClient.connect();
      sshClient.put("/tmp/init-jclouds-script-0", init.render(OsFamily.UNIX));
      expect(sshClient.getUsername()).andReturn("tester").atLeastOnce();
      expect(sshClient.getHostAddress()).andReturn("somewhere.example.com").atLeastOnce();

      // setup script as default user
      expect(sshClient.exec("chmod 755 /tmp/init-jclouds-script-0")).andReturn(new ExecResponse("", "", 0));
      expect(sshClient.exec("ln -fs /tmp/init-jclouds-script-0 jclouds-script-0")).andReturn(
               new ExecResponse("", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 init")).andReturn(new ExecResponse("", "", 0));

      // kick off as current user
      expect(sshClient.exec("/tmp/init-jclouds-script-0 start")).andReturn(new ExecResponse("", "", 0));

      // signal the command completed
      expect(sshClient.exec("/tmp/init-jclouds-script-0 status")).andReturn(new ExecResponse("", "", 1));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stdout")).andReturn(new ExecResponse("out", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 stderr")).andReturn(new ExecResponse("err", "", 0));
      expect(sshClient.exec("/tmp/init-jclouds-script-0 exitstatus")).andReturn(new ExecResponse("1", "", 0));

      sshClient.disconnect();
      replay(sshClient);

      RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete testMe = new RunScriptOnNodeAsInitScriptUsingSshAndBlockUntilComplete(
               statusFactory, timeouts, Functions.forMap(ImmutableMap.of(node, sshClient)),
               eventBus, InitScriptConfigurationForTasks.create().appendIncrementingNumberToAnonymousTaskNames(), node, command,
               new RunScriptOptions().runAsRoot(false));

      assertEquals(testMe.getInitFile(), "/tmp/init-jclouds-script-0");
      assertEquals(testMe.getNode(), node);
      assertEquals(testMe.getStatement(), init);

      testMe.init();
      
      assertEquals(testMe.call(), new ExecResponse("out", "err", 1));

      verify(sshClient);
   }
}
