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

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.InputStream;
import java.util.List;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudservers.domain.Flavor;
import org.jclouds.rackspace.cloudservers.domain.Image;
import org.jclouds.rackspace.cloudservers.domain.Server;
import org.jclouds.rackspace.cloudservers.domain.ServerStatus;
import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.jsch.config.JschSshConnectionModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Injector;

/**
 * Tests behavior of {@code CloudServersConnection}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "cloudservers.CloudServersConnectionLiveTest")
public class CloudServersConnectionLiveTest {

   protected static final String sysRackspaceUser = System.getProperty(PROPERTY_RACKSPACE_USER);
   protected static final String sysRackspaceKey = System.getProperty(PROPERTY_RACKSPACE_KEY);
   CloudServersConnection connection;
   SshConnection.Factory sshFactory;

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
      List<Server> response = connection.listServerDetails();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
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
      List<Flavor> response = connection.listFlavorDetails();
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
      List<Image> response = connection.listImageDetails();
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
   public void testGetImageDetails() throws Exception {
      List<Image> response = connection.listImageDetails();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         Image newDetails = connection.getImageDetails(image.getId());
         assertEquals(image, newDetails);
      }
   }

   @Test(enabled = false)
   // Rackspace Web Hosting issue #118856
   public void testGetImageDetailsNotFound() throws Exception {
      Image newDetails = connection.getImageDetails(12312987);
      assertEquals(Image.NOT_FOUND, newDetails);
   }

   @Test
   public void testGetFlavorDetails() throws Exception {
      List<Flavor> response = connection.listFlavorDetails();
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 0);
      for (Flavor flavor : response) {
         Flavor newDetails = connection.getFlavorDetails(flavor.getId());
         assertEquals(flavor, newDetails);
      }
   }

   public void testGetFlavorDetailsNotFound() throws Exception {
      Flavor newDetails = connection.getFlavorDetails(12312987);
      assertEquals(Flavor.NOT_FOUND, newDetails);
   }

   public void testGetServerDetailsNotFound() throws Exception {
      Server newDetails = connection.getServerDetails(12312987);
      assertEquals(Server.NOT_FOUND, newDetails);
   }

   public void testGetServerDetails() throws Exception {
      List<Server> response = connection.listServerDetails();
      assert null != response;
      long serverCount = response.size();
      assertTrue(serverCount >= 0);
      for (Server server : response) {
         Server newDetails = connection.getServerDetails(server.getId());
         assertEquals(server, newDetails);
      }
   }

   private String serverPrefix = System.getProperty("user.name") + ".cs";

   @Test(timeOut = 5 * 60 * 1000)
   public void testCreateServer() throws Exception {
      Server newDetails = connection.createServer(serverPrefix + "createserver", 2, 1);
      System.err.print(newDetails);
      assertNotNull(newDetails.getAdminPass());
      assertNotNull(newDetails.getHostId());
      assertEquals(newDetails.getStatus(), ServerStatus.BUILD);
      assert newDetails.getProgress() >= 0 : "newDetails.getProgress()" + newDetails.getProgress();
      assertEquals(new Integer(2), newDetails.getImageId());
      assertEquals(new Integer(1), newDetails.getFlavorId());
      assertNotNull(newDetails.getAddresses());
      assertEquals(newDetails.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(newDetails.getAddresses().getPrivateAddresses().size(), 1);

      int serverId = newDetails.getId();
      ServerStatus currentStatus = newDetails.getStatus();
      Server currentDetails = newDetails;
      while (currentStatus != ServerStatus.ACTIVE) {
         Thread.sleep(5 * 1000);
         currentDetails = connection.getServerDetails(serverId);
         System.out.println(currentDetails);
         currentStatus = currentDetails.getStatus();
      }

      InputStream etcPasswd = sshFactory.create(
               newDetails.getAddresses().getPublicAddresses().get(0), 22, "root",
               newDetails.getAdminPass()).get("/etc/passwd");
      String etcPasswdContents = Utils.toStringAndClose(etcPasswd);
      assert etcPasswdContents.indexOf("root") >= 0 : etcPasswdContents;

      connection.deleteServer(serverId);

      currentDetails = connection.getServerDetails(serverId);
      assertEquals(ServerStatus.DELETED, currentDetails.getStatus());
   }

}
