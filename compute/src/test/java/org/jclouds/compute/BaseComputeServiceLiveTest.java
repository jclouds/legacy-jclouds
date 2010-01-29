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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeSet;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Size;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
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
   protected String tag;

   private RetryablePredicate<InetSocketAddress> socketTester;
   private SortedSet<NodeMetadata> nodes;
   protected ComputeServiceContext context;
   protected ComputeService client;
   protected String user;
   protected String password;
   protected Template template;
   protected Map<String, String> keyPair;

   @BeforeGroups(groups = { "live" })
   public void setupClient() throws InterruptedException, ExecutionException, TimeoutException,
            IOException {
      if (tag == null)
         tag = checkNotNull(service, "service");
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      String secretKeyFile;
      try {
         secretKeyFile = checkNotNull(System.getProperty("jclouds.test.ssh.keyfile"),
                  "jclouds.test.ssh.keyfile");
      } catch (NullPointerException e) {
         secretKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
      }
      String secret = Files.toString(new File(secretKeyFile), Charsets.UTF_8);
      assert secret.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + secret;
      context = new ComputeServiceContextFactory().createContext(service, user, password,
               ImmutableSet.of(new Log4JLoggingModule(), getSshModule()));
      Injector injector = Guice.createInjector(getSshModule());
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 60, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
      client = context.getComputeService();
      // keyPair = sshFactory.generateRSAKeyPair("", "");
      keyPair = ImmutableMap.<String, String> of("private", secret, "public", Files.toString(
               new File(secretKeyFile + ".pub"), Charsets.UTF_8));
   }

   abstract protected Module getSshModule();

   @Test(enabled = true)
   public void testCreate() throws Exception {
      try {
         client.destroyNodesWithTag(tag);
      } catch (HttpResponseException e) {
         // TODO hosting.com throws 400 when we try to delete a vApp
      } catch (NoSuchElementException e) {

      }
      template = buildTemplate(client.templateBuilder());

      template
               .getOptions()
               .installPrivateKey(keyPair.get("private"))
               .authorizePublicKey(keyPair.get("public"))
               .runScript(
                        new StringBuilder()//
                                 .append("echo nameserver 208.67.222.222 >> /etc/resolv.conf\n")//
                                 .append("cp /etc/apt/sources.list /etc/apt/sources.list.old\n")//
                                 .append(
                                          "sed 's~us.archive.ubuntu.com~mirror.anl.gov/pub~g' /etc/apt/sources.list.old >/etc/apt/sources.list\n")//
                                 .append("apt-get update\n")//
                                 .append("apt-get install -f -y --force-yes openjdk-6-jdk\n")//
                                 .append("wget -qO/usr/bin/runurl run.alestic.com/runurl\n")//
                                 .append("chmod 755 /usr/bin/runurl\n")//
                                 .toString().getBytes());
      nodes = Sets.newTreeSet(client.runNodesWithTag(tag, 2, template));
      assertEquals(nodes.size(), 2);
      for (NodeMetadata node : nodes) {
         assertNotNull(node.getId());
         assertNotNull(node.getTag());
         assertEquals(node.getTag(), tag);
         assert node.getPublicAddresses().size() >= 1 || node.getPrivateAddresses().size() >= 1 : "no ips in"
                  + node;
         assertNotNull(node.getCredentials());
         if (node.getCredentials().account != null) {
            assertNotNull(node.getCredentials().account);
            assertNotNull(node.getCredentials().key);
            sshPing(node);
         }
      }

      NodeMetadata node1 = nodes.first();
      NodeMetadata node2 = nodes.last();
      // credentials aren't always the same
      // assertEquals(node1.getCredentials(), node2.getCredentials());
      assert !node1.getId().equals(node2.getId());
   }

   protected abstract Template buildTemplate(TemplateBuilder templateBuilder);

   @Test(enabled = true, dependsOnMethods = "testCreate")
   public void testGet() throws Exception {
      NodeSet metadataSet = client.getNodesWithTag(tag);
      for (NodeMetadata node : nodes) {
         metadataSet.remove(node);
         NodeMetadata metadata = client.getNodeMetadata(node);
         assertEquals(metadata.getId(), node.getId());
         assertEquals(metadata.getName(), node.getName());
         assertEquals(metadata.getPrivateAddresses(), node.getPrivateAddresses());
         assertEquals(metadata.getPublicAddresses(), node.getPublicAddresses());
      }
      assert Iterables.all(metadataSet, new Predicate<NodeMetadata>() {
         @Override
         public boolean apply(NodeMetadata input) {
            return input.getState() == NodeState.TERMINATED;
         }
      }) : metadataSet;

   }

   public void testListNodes() throws Exception {
      for (Entry<String, ? extends ComputeMetadata> node : client.getNodes().entrySet()) {
         assertEquals(node.getKey(), node.getValue().getId());
         assert node.getValue().getId() != null;
         assert node.getValue().getLocationId() != null;
         assertEquals(node.getValue().getType(), ComputeType.NODE);
      }
   }

   public void testListImages() throws Exception {
      for (Entry<String, ? extends Image> image : client.getImages().entrySet()) {
         assertEquals(image.getKey(), image.getValue().getId());
         assert image.getValue().getId() != null : image;
         // image.getValue().getLocationId() can be null, if it is a location-free image
         assertEquals(image.getValue().getType(), ComputeType.IMAGE);
      }
   }

   public void testListLocations() throws Exception {
      for (Entry<String, ? extends Location> image : client.getLocations().entrySet()) {
         assertEquals(image.getKey(), image.getValue().getId());
         assert image.getValue().getId() != null : image;
         assert image.getValue().getId() != image.getValue().getParent() : image;
         assert image.getValue().getScope() != null : image;
      }
   }

   public void testListSizes() throws Exception {
      for (Entry<String, ? extends Size> size : client.getSizes().entrySet()) {
         assertEquals(size.getKey(), size.getValue().getId());
         assert size.getValue().getId() != null;
         assert size.getValue().getCores() > 0;
         assert size.getValue().getDisk() > 0;
         assert size.getValue().getRam() > 0;
         assert size.getValue().getSupportedArchitectures() != null;
         assertEquals(size.getValue().getType(), ComputeType.SIZE);
      }
   }

   private void sshPing(NodeMetadata node) throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            doCheckKey(node);
            return;
         } catch (SshException e) {
            try {
               Thread.sleep(10 * 1000);
            } catch (InterruptedException e1) {
            }
            continue;
         }
      }
   }

   private void doCheckKey(NodeMetadata node) throws IOException {
      InetSocketAddress socket = new InetSocketAddress(Iterables.get(node.getPublicAddresses(), 0),
               22);
      socketTester.apply(socket); // TODO add transitionTo option that accepts a socket conection
      // state.
      SshClient ssh = sshFactory.create(socket, node.getCredentials().account, keyPair.get(
               "private").getBytes());
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         ExecResponse exec = ssh.exec("java -version");
         assert exec.getError().indexOf("OpenJDK") != -1 : exec;
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @AfterTest
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      if (nodes != null) {
         client.destroyNodesWithTag(tag);
         for (NodeMetadata node : client.getNodesWithTag(tag)) {
            assert node.getState() == NodeState.TERMINATED : node;
         }
      }
      context.close();
   }

}
