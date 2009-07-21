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
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.rackspace.cloudservers.domain.SharedIpGroup;
import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.jsch.config.JschSshConnectionModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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
         // sometimes this is not present per: Web Hosting #118820
         // assert null != image.getCreated() : image;
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
      assertEquals(sharedIpGroup.getServers(), ImmutableList.of(serverId));
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testCreateSharedIpGroup" })
   void testDeleteSharedIpGroup() {
      if (sharedIpGroupId > 0) {
         connection.deleteSharedIpGroup(sharedIpGroupId);
         SharedIpGroup server = connection.getSharedIpGroup(sharedIpGroupId);
         assertEquals(server, SharedIpGroup.NOT_FOUND);
      }
   }

   private int sharedIpGroupId;

   private String serverPrefix = System.getProperty("user.name") + ".cs";
   private int serverId;
   private String adminPass;
   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");

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
      assertEquals(server.getStatus(), ServerStatus.BUILD);
      blockUntilActive(server);
   }

   private void blockUntilActive(Server server) throws InterruptedException {
      ServerStatus currentStatus = server.getStatus();
      Server currentDetails = server;
      while (currentStatus != ServerStatus.ACTIVE) {
         Thread.sleep(5 * 1000);
         currentDetails = connection.getServer(serverId);
         System.out.println(currentDetails);
         currentStatus = currentDetails.getStatus();
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
      assertEquals(server.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(server.getAddresses().getPrivateAddresses().size(), 1);

      // check metadata
      assertEquals(server.getMetadata(), metadata);
      checkPassOk(server, adminPass);
   }

   /**
    * this tests "personality" as the file looked up was sent during server creation
    */
   private void checkPassOk(Server newDetails, String pass) throws IOException {
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

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testRenameServer() throws Exception {
      Server server = connection.getServer(serverId);
      String oldName = server.getName();
      assertTrue(connection.renameServer(serverId, oldName + "new"));
      blockUntilActive(server);
      assertEquals(oldName + "new", connection.getServer(serverId).getName());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testChangePassword() throws Exception {
      Server server = connection.getServer(serverId);
      assertTrue(connection.changeAdminPass(serverId, "elmo"));
      blockUntilActive(server);
      checkPassOk(connection.getServer(serverId), "elmo");
      this.adminPass = "elmo";
   }

   // TODO test createServer.withSharedIpGroup
   // TODO test createServer.withSharedIp

   // must be last!
   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = { "testChangePassword", "testRenameServer" })
   void deleteServer() {
      if (serverId > 0) {
         connection.deleteServer(serverId);
         Server server = connection.getServer(serverId);
         assertEquals(server, Server.NOT_FOUND);
      }
   }

}
