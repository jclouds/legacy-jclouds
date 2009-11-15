/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.withDetails;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rackspace.RackspacePropertiesBuilder;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CloudServersClient}
 * 
 * @author Adrian Cole
 */
// disabled [Web Hosting #129069
@Test(groups = "live", sequential = true, testName = "cloudservers.CloudServersClientLiveTest")
public class CloudServersClientLiveTest {

   protected CloudServersClient client;
   protected SshClient.Factory sshFactory;
   private RetryablePredicate<InetSocketAddress> socketTester;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new CloudServersContextBuilder(new RackspacePropertiesBuilder(user,
               password).build()).withModules(new Log4JLoggingModule(), new JschSshClientModule())
               .buildInjector();
      client = injector.getInstance(CloudServersClient.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<InetSocketAddress>(socketOpen, 120, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   public void testListServers() throws Exception {

      List<Server> response = client.listServers();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

   }

   public void testListServersDetail() throws Exception {
      List<Server> response = client.listServers(withDetails());
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
   }

   public void testListImages() throws Exception {
      List<Image> response = client.listImages();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 1);
      for (Image image : response) {
         assertTrue(image.getId() >= 0);
         assert null != image.getName() : image;
      }

   }

   public void testListImagesDetail() throws Exception {
      List<Image> response = client.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         assertTrue(image.getId() >= 1);
         assert null != image.getName() : image;
         // bug in image id: 14362 assert null != image.getCreated() : image;
         // bug in image id: 14362 assert null != image.getUpdated() : image;
         // bug in image id: 14362 assert null != image.getStatus() : image;
      }
   }

   // Rackspace Web Hosting issue #118856
   public void testGetImagesDetail() throws Exception {
      List<Image> response = client.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         Image newDetails = client.getImage(image.getId());
         assertEquals(image.getId(), newDetails.getId());
         assertEquals(image.getName(), newDetails.getName());
         // don't check created,serverId, status or last updated! these can change during testing
      }
   }

   // Rackspace Web Hosting issue #118856
   public void testGetImageDetailsNotFound() throws Exception {
      Image newDetails = client.getImage(12312987);
      assertEquals(Image.NOT_FOUND, newDetails);
   }

   public void testGetServerDetailsNotFound() throws Exception {
      Server newDetails = client.getServer(12312987);
      assertEquals(Server.NOT_FOUND, newDetails);
   }

   public void testGetServersDetail() throws Exception {
      List<Server> response = client.listServers(withDetails());
      assert null != response;
      long serverCount = response.size();
      assertTrue(serverCount >= 0);
      for (Server server : response) {
         Server newDetails = client.getServer(server.getId());
         assertEquals(newDetails.getId(), server.getId());
         // other verifications might fail due to other testing
      }
   }

   public void testListFlavors() throws Exception {
      List<Flavor> response = client.listFlavors();
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 1);
      for (Flavor flavor : response) {
         assertTrue(flavor.getId() >= 0);
         assert null != flavor.getName() : flavor;
      }

   }

   public void testListFlavorsDetail() throws Exception {
      List<Flavor> response = client.listFlavors(withDetails());
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 0);
      for (Flavor flavor : response) {
         assertTrue(flavor.getId() >= 1);
         assert null != flavor.getName() : flavor;
         assert null != flavor.getDisk() : flavor;
         assert null != flavor.getRam() : flavor;
      }
   }

   public void testGetFlavorsDetail() throws Exception {
      List<Flavor> response = client.listFlavors(withDetails());
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 0);
      for (Flavor flavor : response) {
         Flavor newDetails = client.getFlavor(flavor.getId());
         assertEquals(flavor, newDetails);
      }
   }

   public void testGetFlavorDetailsNotFound() throws Exception {
      Flavor newDetails = client.getFlavor(12312987);
      assertEquals(Flavor.NOT_FOUND, newDetails);
   }

   public void testListSharedIpGroups() throws Exception {
      List<SharedIpGroup> response = client.listSharedIpGroups();
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         assertTrue(sharedIpGroup.getId() >= 0);
         assert null != sharedIpGroup.getName() : sharedIpGroup;
      }

   }

   public void testListSharedIpGroupsDetail() throws Exception {
      List<SharedIpGroup> response = client.listSharedIpGroups(withDetails());
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         assertTrue(sharedIpGroup.getId() >= 1);
         assert null != sharedIpGroup.getName() : sharedIpGroup;
         assert null != sharedIpGroup.getServers() : sharedIpGroup;
      }
   }

   public void testGetSharedIpGroupsDetail() throws Exception {
      List<SharedIpGroup> response = client.listSharedIpGroups(withDetails());
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         SharedIpGroup newDetails = client.getSharedIpGroup(sharedIpGroup.getId());
         assertEquals(sharedIpGroup, newDetails);
      }
   }

   public void testGetSharedIpGroupDetailsNotFound() throws Exception {
      SharedIpGroup newDetails = client.getSharedIpGroup(12312987);
      assertEquals(SharedIpGroup.NOT_FOUND, newDetails);
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testServerDetails")
   public void testCreateSharedIpGroup() throws Exception {
      SharedIpGroup sharedIpGroup = null;
      while (sharedIpGroup == null) {
         String sharedIpGroupName = serverPrefix + "createSharedIpGroup"
                  + new SecureRandom().nextInt();
         try {
            sharedIpGroup = client.createSharedIpGroup(sharedIpGroupName, withServer(serverId));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw e;
         }
      }
      assertNotNull(sharedIpGroup.getName());
      sharedIpGroupId = sharedIpGroup.getId();
      // Response doesn't include the server id Web Hosting #119311
      // assertEquals(sharedIpGroup.getServers(), ImmutableList.of(serverId));
   }

   private int sharedIpGroupId;

   private String serverPrefix = System.getProperty("user.name") + ".cs";
   private int serverId;
   private String adminPass;
   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
   private InetAddress ip;
   private int serverId2;
   private String adminPass2;

   public void testCreateServer() throws Exception {
      int imageId = 2;
      int flavorId = 1;
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            System.out.printf("%d: running instance%n", System.currentTimeMillis());
            server = client.createServer(serverName, imageId, flavorId, withFile(
                     "/etc/jclouds.txt", "rackspace".getBytes()).withMetadata(metadata));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw e;
         }
      }
      assertNotNull(server.getAdminPass());
      serverId = server.getId();
      adminPass = server.getAdminPass();
      ip = server.getAddresses().getPublicAddresses().iterator().next();
      assertEquals(server.getStatus(), ServerStatus.BUILD);
      blockUntilServerActive(serverId);
   }

   private void blockUntilServerActive(int serverId) throws InterruptedException {
      Server currentDetails = null;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.ACTIVE; currentDetails = client
               .getServer(serverId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(1 * 1000);
      }
      System.out.printf("%d: %s awaiting ssh service to start%n", System.currentTimeMillis(),
               currentDetails.getAddresses().getPublicAddresses().first());
      assert socketTester.apply(new InetSocketAddress(currentDetails.getAddresses()
               .getPublicAddresses().first(), 22));
      System.out.printf("%d: %s ssh service started%n", System.currentTimeMillis(), currentDetails
               .getAddresses().getPublicAddresses().first());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testServerDetails() throws Exception {
      Server server = client.getServer(serverId);

      assertNotNull(server.getHostId());
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
      assert server.getProgress() >= 0 : "newDetails.getProgress()" + server.getProgress();
      assertEquals(new Integer(2), server.getImageId());
      assertEquals(new Integer(1), server.getFlavorId());
      assertNotNull(server.getAddresses());
      // listAddresses tests..
      assertEquals(client.listAddresses(serverId), server.getAddresses());
      assertEquals(server.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(client.listPublicAddresses(serverId), server.getAddresses().getPublicAddresses());
      assertEquals(server.getAddresses().getPrivateAddresses().size(), 1);
      assertEquals(client.listPrivateAddresses(serverId), server.getAddresses()
               .getPrivateAddresses());

      // check metadata
      assertEquals(server.getMetadata(), metadata);

      checkPassOk(server, adminPass);
   }

   /**
    * this tests "personality" as the file looked up was sent during server creation
    */
   private void checkPassOk(Server newDetails, String pass) throws IOException {
      try {
         doCheckPass(newDetails, pass);
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         doCheckPass(newDetails, pass);
      }
   }

   private void doCheckPass(Server newDetails, String pass) throws IOException {
      SshClient connection = sshFactory.create(new InetSocketAddress(newDetails.getAddresses()
               .getPublicAddresses().first(), 22), "root", pass);
      try {
         connection.connect();
         InputStream etcPasswd = connection.get("/etc/jclouds.txt");
         String etcPasswdContents = Utils.toStringAndClose(etcPasswd);
         assertEquals("rackspace", etcPasswdContents.trim());
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   private ExecResponse exec(Server details, String pass, String command) throws IOException {
      SshClient connection = sshFactory.create(new InetSocketAddress(details.getAddresses()
               .getPublicAddresses().first(), 22), "root", pass);
      try {
         connection.connect();
         return connection.exec(command);
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateSharedIpGroup")
   public void testCreateServerIp() throws Exception {
      int imageId = 2;
      int flavorId = 1;
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            server = client.createServer(serverName, imageId, flavorId, withFile(
                     "/etc/jclouds.txt", "rackspace".getBytes()).withMetadata(metadata)
                     .withSharedIpGroup(sharedIpGroupId).withSharedIp(ip));
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw e;
         }
      }
      assertNotNull(server.getAdminPass());
      serverId2 = server.getId();
      adminPass2 = server.getAdminPass();
      blockUntilServerActive(serverId2);
      assertIpConfigured(server, adminPass2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses()
               + " doesn't contain " + ip;
      assertEquals(server.getSharedIpGroupId(), new Integer(sharedIpGroupId));
   }

   private void assertIpConfigured(Server server, String password) {
      try {
         ExecResponse response = exec(server, password, "ifconfig -a");
         assert response.getOutput().indexOf(ip.getHostAddress()) > 0 : String.format(
                  "server %s didn't get ip %s%n%s", server, ip, response);
      } catch (Exception e) {
         e.printStackTrace();
      } catch (AssertionError e) {
         e.printStackTrace();
      }
   }

   private void assertIpNotConfigured(Server server, String password) {
      try {
         ExecResponse response = exec(server, password, "ifconfig -a");
         assert response.getOutput().indexOf(ip.getHostAddress()) == -1 : String.format(
                  "server %s still has get ip %s%n%s", server, ip, response);
      } catch (Exception e) {
         e.printStackTrace();
      } catch (AssertionError e) {
         e.printStackTrace();
      }
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testUnshare")
   public void testShareConfig() throws Exception {
      client.shareIp(ip, serverId2, sharedIpGroupId, true);
      blockUntilServerActive(serverId2);
      Server server = client.getServer(serverId2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpConfigured(server, adminPass2);
      unshare();
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testShareConfig")
   public void testShareNoConfig() throws Exception {
      client.shareIp(ip, serverId2, sharedIpGroupId, false);
      blockUntilServerActive(serverId2);
      Server server = client.getServer(serverId2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpNotConfigured(server, adminPass2);
      unshare();
   }

   private void unshare() throws Exception {
      client.unshareIp(ip, serverId2);
      blockUntilServerActive(serverId2);
      Server server = client.getServer(serverId2);
      assert !server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpNotConfigured(server, adminPass2);
   }

   @AfterTest
   void deleteServers() {
      if (serverId > 0) {
         client.deleteServer(serverId);
      }
      if (serverId2 > 0) {
         client.deleteServer(serverId2);
      }
      if (sharedIpGroupId > 0) {
         client.deleteSharedIpGroup(sharedIpGroupId);
      }
   }
}
