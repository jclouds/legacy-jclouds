/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import static org.jclouds.rackspace.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.rackspace.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.jclouds.rackspace.cloudservers.options.ListOptions.Builder.withDetails;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudservers.domain.BackupSchedule;
import org.jclouds.rackspace.cloudservers.domain.DailyBackup;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.ImageStatus;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.jclouds.rackspace.cloudservers.domain.WeeklyBackup;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshConnectionModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CloudServersConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "cloudservers.CloudServersConnectionLiveTest")
public class CloudServersConnectionLiveTest {

   protected static final String sysRackspaceUser = System.getProperty(PROPERTY_RACKSPACE_USER);
   protected static final String sysRackspaceKey = System.getProperty(PROPERTY_RACKSPACE_KEY);
   protected CloudServersConnection connection;
   protected SshConnection.Factory sshFactory;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      Injector injector = CloudServersContextBuilder.newBuilder(sysRackspaceUser, sysRackspaceKey)
               .withModules(new Log4JLoggingModule(), new JschSshConnectionModule())
               .withJsonDebug().buildInjector();
      connection = injector.getInstance(CloudServersConnection.class);
      sshFactory = injector.getInstance(SshConnection.Factory.class);

   }

   @Test
   public void testListServers() throws Exception {

      List<Server> response = connection.listServers();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

   }

   @Test
   public void testListServersDetail() throws Exception {
      List<Server> response = connection.listServers(withDetails());
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
   }

   @Test
   public void testListImages() throws Exception {
      List<Image> response = connection.listImages();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 1);
      for (Image image : response) {
         assertTrue(image.getId() >= 0);
         assert null != image.getName() : image;
      }

   }

   @Test
   public void testListImagesDetail() throws Exception {
      List<Image> response = connection.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         assertTrue(image.getId() >= 1);
         assert null != image.getName() : image;
         assert null != image.getCreated() : image;
         assert null != image.getUpdated() : image;
         assert null != image.getStatus() : image;
      }
   }

   @Test(enabled = false)
   // Rackspace Web Hosting issue #118856
   public void testGetImagesDetail() throws Exception {
      List<Image> response = connection.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         Image newDetails = connection.getImage(image.getId());
         assertEquals(image, newDetails);
      }
   }

   @Test(enabled = false)
   // Rackspace Web Hosting issue #118856
   public void testGetImageDetailsNotFound() throws Exception {
      Image newDetails = connection.getImage(12312987);
      assertEquals(Image.NOT_FOUND, newDetails);
   }

   public void testGetServerDetailsNotFound() throws Exception {
      Server newDetails = connection.getServer(12312987);
      assertEquals(Server.NOT_FOUND, newDetails);
   }

   public void testGetServersDetail() throws Exception {
      List<Server> response = connection.listServers(withDetails());
      assert null != response;
      long serverCount = response.size();
      assertTrue(serverCount >= 0);
      for (Server server : response) {
         Server newDetails = connection.getServer(server.getId());
         assertEquals(server, newDetails);
      }
   }

   @Test
   public void testListFlavors() throws Exception {
      List<Flavor> response = connection.listFlavors();
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 1);
      for (Flavor flavor : response) {
         assertTrue(flavor.getId() >= 0);
         assert null != flavor.getName() : flavor;
      }

   }

   @Test
   public void testListFlavorsDetail() throws Exception {
      List<Flavor> response = connection.listFlavors(withDetails());
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

   @Test
   public void testGetFlavorsDetail() throws Exception {
      List<Flavor> response = connection.listFlavors(withDetails());
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 0);
      for (Flavor flavor : response) {
         Flavor newDetails = connection.getFlavor(flavor.getId());
         assertEquals(flavor, newDetails);
      }
   }

   public void testGetFlavorDetailsNotFound() throws Exception {
      Flavor newDetails = connection.getFlavor(12312987);
      assertEquals(Flavor.NOT_FOUND, newDetails);
   }

   @Test
   public void testListSharedIpGroups() throws Exception {
      List<SharedIpGroup> response = connection.listSharedIpGroups();
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         assertTrue(sharedIpGroup.getId() >= 0);
         assert null != sharedIpGroup.getName() : sharedIpGroup;
      }

   }

   @Test
   public void testListSharedIpGroupsDetail() throws Exception {
      List<SharedIpGroup> response = connection.listSharedIpGroups(withDetails());
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         assertTrue(sharedIpGroup.getId() >= 1);
         assert null != sharedIpGroup.getName() : sharedIpGroup;
         assert null != sharedIpGroup.getServers() : sharedIpGroup;
      }
   }

   @Test
   public void testGetSharedIpGroupsDetail() throws Exception {
      List<SharedIpGroup> response = connection.listSharedIpGroups(withDetails());
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         SharedIpGroup newDetails = connection.getSharedIpGroup(sharedIpGroup.getId());
         assertEquals(sharedIpGroup, newDetails);
      }
   }

   public void testGetSharedIpGroupDetailsNotFound() throws Exception {
      SharedIpGroup newDetails = connection.getSharedIpGroup(12312987);
      assertEquals(SharedIpGroup.NOT_FOUND, newDetails);
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testCreateSharedIpGroup() throws Exception {
      SharedIpGroup sharedIpGroup = null;
      while (sharedIpGroup == null) {
         String sharedIpGroupName = serverPrefix + "createSharedIpGroup"
                  + new SecureRandom().nextInt();
         try {
            sharedIpGroup = connection.createSharedIpGroup(sharedIpGroupName, withServer(serverId));
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

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateServer() throws Exception {
      int imageId = 2;
      int flavorId = 1;
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            server = connection.createServer(serverName, imageId, flavorId, withFile(
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
      for (currentDetails = connection.getServer(serverId); currentDetails.getStatus() != ServerStatus.ACTIVE; currentDetails = connection
               .getServer(serverId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
      /**
       * [Web Hosting #119335]
       */
      System.out.printf("awaiting daemons to start %n%s%n", currentDetails);
      Thread.sleep(10 * 1000);
   }

   private void blockUntilImageActive(int imageId) throws InterruptedException {
      Image currentDetails = null;
      for (currentDetails = connection.getImage(imageId); currentDetails.getStatus() != ImageStatus.ACTIVE; currentDetails = connection
               .getImage(imageId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testServerDetails() throws Exception {
      Server server = connection.getServer(serverId);

      assertNotNull(server.getHostId());
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
      assert server.getProgress() >= 0 : "newDetails.getProgress()" + server.getProgress();
      assertEquals(new Integer(2), server.getImageId());
      assertEquals(new Integer(1), server.getFlavorId());
      assertNotNull(server.getAddresses());
      // listAddresses tests..
      assertEquals(connection.listAddresses(serverId), server.getAddresses());
      assertEquals(server.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(connection.listPublicAddresses(serverId), server.getAddresses()
               .getPublicAddresses());
      assertEquals(server.getAddresses().getPrivateAddresses().size(), 1);
      assertEquals(connection.listPrivateAddresses(serverId), server.getAddresses()
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
      SshConnection connection = sshFactory.create(newDetails.getAddresses().getPublicAddresses()
               .get(0), 22, "root", pass);
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
      SshConnection connection = sshFactory.create(details.getAddresses().getPublicAddresses().get(
               0), 22, "root", pass);
      try {
         connection.connect();
         return connection.exec(command);
      } finally {
         if (connection != null)
            connection.disconnect();
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testRenameServer() throws Exception {
      Server server = connection.getServer(serverId);
      String oldName = server.getName();
      assertTrue(connection.renameServer(serverId, oldName + "new"));
      blockUntilServerActive(serverId);
      assertEquals(oldName + "new", connection.getServer(serverId).getName());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testChangePassword() throws Exception {
      assertTrue(connection.changeAdminPass(serverId, "elmo"));
      blockUntilServerActive(serverId);
      checkPassOk(connection.getServer(serverId), "elmo");
      this.adminPass = "elmo";
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateSharedIpGroup")
   public void testCreateServerIp() throws Exception {
      int imageId = 2;
      int flavorId = 1;
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            server = connection.createServer(serverName, imageId, flavorId, withFile(
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

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServerIp")
   public void testUnshare() throws Exception {
      connection.unshareIp(ip, serverId2);
      blockUntilServerActive(serverId2);
      Server server = connection.getServer(serverId2);
      assert !server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpNotConfigured(server, adminPass2);
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

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testUnshare")
   public void testShareConfig() throws Exception {
      connection.shareIp(ip, serverId2, sharedIpGroupId, true);
      blockUntilServerActive(serverId2);
      Server server = connection.getServer(serverId2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpConfigured(server, adminPass2);
      testUnshare();
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testShareConfig")
   public void testShareNoConfig() throws Exception {
      connection.shareIp(ip, serverId2, sharedIpGroupId, false);
      blockUntilServerActive(serverId2);
      Server server = connection.getServer(serverId2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpNotConfigured(server, adminPass2);
      testUnshare();
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testShareNoConfig")
   public void testBackup() throws Exception {
      assertEquals(new BackupSchedule(), connection.listBackupSchedule(serverId));
      BackupSchedule dailyWeekly = new BackupSchedule();
      dailyWeekly.setEnabled(true);
      dailyWeekly.setWeekly(WeeklyBackup.FRIDAY);
      dailyWeekly.setDaily(DailyBackup.H_0400_0600);
      assertEquals(true, connection.replaceBackupSchedule(serverId, dailyWeekly));
      connection.deleteBackupSchedule(serverId);
      // disables, doesn't delete: Web Hosting #119571
      assertEquals(connection.listBackupSchedule(serverId).isEnabled(), false);
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testBackup")
   public void testCreateImage() throws Exception {
      Image image = connection.createImageFromServer("hoofie", serverId);
      assertEquals("hoofie", image.getName());
      assertEquals(new Integer(serverId), image.getServerId());
      int imageId = image.getId();
      blockUntilImageActive(imageId);
   }

   // must be last!. do not rely on positional order.
   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateImage")
   void deleteServers() {
      if (serverId > 0) {
         connection.deleteServer(serverId);
         Server server = connection.getServer(serverId);
         assertEquals(server, Server.NOT_FOUND);
      }
      if (serverId2 > 0) {
         connection.deleteServer(serverId2);
         Server server = connection.getServer(serverId2);
         assertEquals(server, Server.NOT_FOUND);
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "deleteServers" })
   void testDeleteSharedIpGroup() {
      if (sharedIpGroupId > 0) {
         connection.deleteSharedIpGroup(sharedIpGroupId);
         SharedIpGroup server = connection.getSharedIpGroup(sharedIpGroupId);
         assertEquals(server, SharedIpGroup.NOT_FOUND);
      }
   }
}
