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
package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.CreateNodeResponse;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.RunNodeOptions;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "compute.ComputeServiceLiveTest")
public abstract class BaseComputeServiceLiveTest {
   @BeforeClass
   abstract public void setServiceDefaults();

   protected String service;
   protected SshClient.Factory sshFactory;
   protected RunNodeOptions options = RunNodeOptions.Builder.openPorts(22);
   protected String nodeName;

   private RetryablePredicate<InetSocketAddress> socketTester;
   private CreateNodeResponse node;
   protected ComputeServiceContext context;
   protected ComputeService client;
   protected String user;
   protected String password;
   private Template template;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      if (nodeName == null) nodeName = checkNotNull(service, "service");
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      context = new ComputeServiceContextFactory().createContext(service, user, password,
               ImmutableSet.of(new Log4JLoggingModule(), getSshModule()));
      Injector injector = Guice.createInjector(getSshModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
      client = context.getComputeService();
   }

   protected boolean canRunScript(Template template) {
      return template.getImage().getOsFamily() == OsFamily.UBUNTU
               || template.getImage().getOsFamily() == OsFamily.JEOS;
   }

   abstract protected Module getSshModule();

   @Test(enabled = true)
   public void testCreate() throws Exception {
      try {
         client.destroyNode(Iterables.find(client.listNodes(), new Predicate<ComputeMetadata>() {
            @Override
            public boolean apply(ComputeMetadata input) {
               return input.getName().equals(nodeName);
            }
         }));
      } catch (HttpResponseException e) {
         // TODO hosting.com throws 400 when we try to delete a vApp
      } catch (NoSuchElementException e) {

      }
      template = buildTemplate(client.templateBuilder());

      if (canRunScript(template))
         options.runScript(new ScriptBuilder()
                  // update add dns and install jdk
                  .addStatement(
                           Statements.exec("echo nameserver 208.67.222.222 >> /etc/resolv.conf"))
                  .addStatement(Statements.exec("apt-get update"))//
                  .addStatement(Statements.exec("apt-get upgrade -y"))//
                  .addStatement(Statements.exec("apt-get install -y openjdk-6-jdk"))//
                  .addStatement(Statements.exec("wget -qO/usr/bin/runurl run.alestic.com/runurl"))//
                  .addStatement(Statements.exec("chmod 755 /usr/bin/runurl"))//
                  .build(org.jclouds.scriptbuilder.domain.OsFamily.UNIX).getBytes());
      node = client.runNode(nodeName, template, options);
      assertNotNull(node.getId());
      assertNotNull(node.getName());
      assert node.getPublicAddresses().size() >= 1: "no public ips in" + node;
      assertNotNull(node.getCredentials());
      if (node.getCredentials().account != null) {
         assertNotNull(node.getCredentials().account);
         assertNotNull(node.getCredentials().key);
         sshPing();
      }
   }

   protected abstract Template buildTemplate(TemplateBuilder templateBuilder);

   @Test(enabled = true, dependsOnMethods = "testCreate")
   public void testGet() throws Exception {
      NodeMetadata metadata = client.getNodeMetadata(node);
      assertEquals(metadata.getId(), node.getId());
      assertEquals(metadata.getName(), node.getName());
      assertEquals(metadata.getPrivateAddresses(), node.getPrivateAddresses());
      assertEquals(metadata.getPublicAddresses(), node.getPublicAddresses());
   }

   public void testListNodes() throws Exception {
      for (ComputeMetadata node : client.listNodes()) {
         assert node.getId() != null;
         assert node.getLocation() != null;
         assertEquals(node.getType(), ComputeType.NODE);
      }
   }

   public void testListImages() throws Exception {
      for (Image image : client.listImages()) {
         assert image.getId() != null : image;
         assert image.getLocation() != null : image;
      }
   }

   public void testListSizes() throws Exception {
      for (Size size : client.listSizes()) {
         assert size.getCores() > 0 : size;
      }
   }

   private void sshPing() throws IOException {
      try {
         doCheckKey();
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         doCheckKey();
      }
   }

   private void doCheckKey() throws IOException {
      InetSocketAddress socket = new InetSocketAddress(node.getPublicAddresses().last(), 22);
      socketTester.apply(socket);
      SshClient ssh = node.getCredentials().key.startsWith("-----BEGIN RSA PRIVATE KEY-----") ? sshFactory
               .create(socket, node.getCredentials().account, node.getCredentials().key.getBytes())
               : sshFactory
                        .create(socket, node.getCredentials().account, node.getCredentials().key);
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         if (canRunScript(template))
            System.out.println(ssh.exec("java -version"));
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (node != null)
         client.destroyNode(node);
      context.close();
   }

}
