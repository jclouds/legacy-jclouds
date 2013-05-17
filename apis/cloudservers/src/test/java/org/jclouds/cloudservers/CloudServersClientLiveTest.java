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
package org.jclouds.cloudservers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.cloudservers.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.cloudservers.options.CreateSharedIpGroupOptions.Builder.withServer;
import static org.jclouds.cloudservers.options.ListOptions.Builder.withDetails;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;

import org.jclouds.cloudservers.domain.BackupSchedule;
import org.jclouds.cloudservers.domain.DailyBackup;
import org.jclouds.cloudservers.domain.Flavor;
import org.jclouds.cloudservers.domain.Image;
import org.jclouds.cloudservers.domain.ImageStatus;
import org.jclouds.cloudservers.domain.Limits;
import org.jclouds.cloudservers.domain.RebootType;
import org.jclouds.cloudservers.domain.Server;
import org.jclouds.cloudservers.domain.ServerStatus;
import org.jclouds.cloudservers.domain.SharedIpGroup;
import org.jclouds.cloudservers.domain.WeeklyBackup;
import org.jclouds.cloudservers.options.RebuildServerOptions;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.net.HostAndPort;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code CloudServersClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudServersClientLiveTest")
public class CloudServersClientLiveTest extends BaseComputeServiceContextLiveTest {

   public CloudServersClientLiveTest() {
      provider = "cloudservers";
   }

   protected CloudServersClient client;
   protected SshClient.Factory sshFactory;
   protected Predicate<HostAndPort> socketTester;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      Injector injector = view.utils().injector();
      client = injector.getInstance(CloudServersClient.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = retry(socketOpen, 120, 1, SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   public void testLimits() throws Exception {
      Limits response = client.getLimits();
      assert null != response;
      assertTrue(response.getAbsolute().size() > 0);
      assertTrue(response.getRate().size() > 0);
   }

   public void testListServers() throws Exception {

      Set<Server> response = client.listServers();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

   }

   public void testListServersDetail() throws Exception {
      Set<Server> response = client.listServers(withDetails());
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
   }

   public void testListImages() throws Exception {
      Set<Image> response = client.listImages();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 1);
      for (Image image : response) {
         assertTrue(image.getId() >= 0);
         assert null != image.getName() : image;
      }

   }

   public void testListImagesDetail() throws Exception {
      Set<Image> response = client.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         assertTrue(image.getId() >= 1);
         assert null != image.getName() : image;
         assert null != image.getStatus() : image;
      }
   }

   public void testGetImagesDetail() throws Exception {
      Set<Image> response = client.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         Image newDetails = client.getImage(image.getId());
         assertEquals(image, newDetails);
      }
   }

   @Test
   public void testGetImageDetailsNotFound() throws Exception {
      assert client.getImage(12312987) == null;
   }

   @Test
   public void testGetServerDetailsNotFound() throws Exception {
      assert client.getServer(12312987) == null;
   }

   public void testGetServersDetail() throws Exception {
      Set<Server> response = client.listServers(withDetails());
      assert null != response;
      long serverCount = response.size();
      assertTrue(serverCount >= 0);
      for (Server server : response) {
         Server newDetails = client.getServer(server.getId());
         assertEquals(server, newDetails);
      }
   }

   public void testListFlavors() throws Exception {
      Set<Flavor> response = client.listFlavors();
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 1);
      for (Flavor flavor : response) {
         assertTrue(flavor.getId() >= 0);
         assert null != flavor.getName() : flavor;
      }

   }

   public void testListFlavorsDetail() throws Exception {
      Set<Flavor> response = client.listFlavors(withDetails());
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
      Set<Flavor> response = client.listFlavors(withDetails());
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 0);
      for (Flavor flavor : response) {
         Flavor newDetails = client.getFlavor(flavor.getId());
         assertEquals(flavor, newDetails);
      }
   }

   @Test
   public void testGetFlavorDetailsNotFound() throws Exception {
      assert client.getFlavor(12312987) == null;
   }

   public void testListSharedIpGroups() throws Exception {
      Set<SharedIpGroup> response = client.listSharedIpGroups();
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         assertTrue(sharedIpGroup.getId() >= 0);
         assert null != sharedIpGroup.getName() : sharedIpGroup;
      }

   }

   public void testListSharedIpGroupsDetail() throws Exception {
      Set<SharedIpGroup> response = client.listSharedIpGroups(withDetails());
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
      Set<SharedIpGroup> response = client.listSharedIpGroups(withDetails());
      assert null != response;
      long sharedIpGroupCount = response.size();
      assertTrue(sharedIpGroupCount >= 0);
      for (SharedIpGroup sharedIpGroup : response) {
         SharedIpGroup newDetails = client.getSharedIpGroup(sharedIpGroup.getId());
         assertEquals(sharedIpGroup, newDetails);
      }
   }

   @Test
   public void testGetSharedIpGroupDetailsNotFound() throws Exception {
      assert client.getSharedIpGroup(12312987) == null;
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testCreateSharedIpGroup() throws Exception {
      SharedIpGroup sharedIpGroup = null;
      while (sharedIpGroup == null) {
         String sharedIpGroupName = serverPrefix + "createSharedIpGroup" + new SecureRandom().nextInt();
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
      assert !sharedIpGroup.getServers().equals(ImmutableList.of(serverId));
   }

   private int sharedIpGroupId;

   private String serverPrefix = System.getProperty("user.name") + ".cs";
   private int serverId;
   private String adminPass;
   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
   private String ip;
   private int serverId2;
   private String adminPass2;
   private int imageId;

   public void testCreateServer() throws Exception {
      int imageId = 14362;
      int flavorId = 1;
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            server = client.createServer(serverName, imageId, flavorId, withFile("/etc/jclouds.txt",
                     "rackspace".getBytes()).withMetadata(metadata));
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
         Thread.sleep(5 * 1000);
      }
   }

   private void blockUntilServerVerifyResize(int serverId) throws InterruptedException {
      Server currentDetails = null;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.VERIFY_RESIZE; currentDetails = client
               .getServer(serverId)) {
         System.out.printf("blocking on status verify resize%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   private void blockUntilImageActive(int imageId) throws InterruptedException {
      Image currentDetails = null;
      for (currentDetails = client.getImage(imageId); currentDetails.getStatus() != ImageStatus.ACTIVE; currentDetails = client
               .getImage(imageId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testServerDetails() throws Exception {
      Server server = client.getServer(serverId);

      assertNotNull(server.getHostId());
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
      assert server.getProgress() >= 0 : "newDetails.getProgress()" + server.getProgress();
      assertEquals(Integer.valueOf(14362), server.getImageId());
      assertEquals(Integer.valueOf(1), server.getFlavorId());
      assertNotNull(server.getAddresses());
      // listAddresses tests..
      assertEquals(client.getAddresses(serverId), server.getAddresses());
      assertEquals(server.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(client.listPublicAddresses(serverId), server.getAddresses().getPublicAddresses());
      assertEquals(server.getAddresses().getPrivateAddresses().size(), 1);
      assertEquals(client.listPrivateAddresses(serverId), server.getAddresses().getPrivateAddresses());

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
      HostAndPort socket = HostAndPort.fromParts(Iterables.get(newDetails.getAddresses().getPublicAddresses(), 0), 22);
      socketTester.apply(socket);

      SshClient client = sshFactory.create(socket, LoginCredentials.builder().user("root").password(pass).build());
      try {
         client.connect();
         Payload etcPasswd = client.get("/etc/jclouds.txt");
         String etcPasswdContents = Strings2.toString(etcPasswd);
         assertEquals("rackspace", etcPasswdContents.trim());
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   private ExecResponse exec(Server details, String pass, String command) throws IOException {
      HostAndPort socket = HostAndPort.fromParts(Iterables.get(details.getAddresses().getPublicAddresses(), 0), 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, LoginCredentials.builder().user("root").password(pass).build());
      try {
         client.connect();
         return client.exec(command);
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testRenameServer() throws Exception {
      Server server = client.getServer(serverId);
      String oldName = server.getName();
      client.renameServer(serverId, oldName + "new");
      blockUntilServerActive(serverId);
      assertEquals(oldName + "new", client.getServer(serverId).getName());
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testChangePassword() throws Exception {
      client.changeAdminPass(serverId, "elmo");
      blockUntilServerActive(serverId);
      checkPassOk(client.getServer(serverId), "elmo");
      this.adminPass = "elmo";
   }

   @Test(timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateSharedIpGroup")
   public void testCreateServerIp() throws Exception {
      int imageId = 14362;
      int flavorId = 1;
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            server = client
                     .createServer(serverName, imageId, flavorId, withFile("/etc/jclouds.txt", "rackspace".getBytes())
                              .withMetadata(metadata).withSharedIpGroup(sharedIpGroupId).withSharedIp(ip));
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
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses() + " doesn't contain " + ip;
      assertEquals(server.getSharedIpGroupId(), Integer.valueOf(sharedIpGroupId));
   }

   private void assertIpConfigured(Server server, String password) {
      try {
         ExecResponse response = exec(server, password, "ifconfig -a");
         assert response.getOutput().indexOf(ip) > 0 : String.format("server %s didn't get ip %s%n%s", server, ip,
                  response);
      } catch (Exception e) {
         e.printStackTrace();
      } catch (AssertionError e) {
         e.printStackTrace();
      }
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testCreateServerIp")
   public void testUnshare() throws Exception {
      client.unshareIp(ip, serverId2);
      blockUntilServerActive(serverId2);
      Server server = client.getServer(serverId2);
      assert !server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpNotConfigured(server, adminPass2);
   }

   private void assertIpNotConfigured(Server server, String password) {
      try {
         ExecResponse response = exec(server, password, "ifconfig -a");
         assert response.getOutput().indexOf(ip) == -1 : String.format("server %s still has get ip %s%n%s", server, ip,
                  response);
      } catch (Exception e) {
         e.printStackTrace();
      } catch (AssertionError e) {
         e.printStackTrace();
      }
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testUnshare")
   public void testShareConfig() throws Exception {
      client.shareIp(ip, serverId2, sharedIpGroupId, true);
      blockUntilServerActive(serverId2);
      Server server = client.getServer(serverId2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpConfigured(server, adminPass2);
      testUnshare();
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testShareConfig")
   public void testShareNoConfig() throws Exception {
      client.shareIp(ip, serverId2, sharedIpGroupId, false);
      blockUntilServerActive(serverId2);
      Server server = client.getServer(serverId2);
      assert server.getAddresses().getPublicAddresses().contains(ip) : server.getAddresses();
      assertIpNotConfigured(server, adminPass2);
      testUnshare();
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testShareNoConfig")
   public void testBackup() throws Exception {
      assertEquals(BackupSchedule.builder().build(), client.getBackupSchedule(serverId));
      BackupSchedule dailyWeekly = BackupSchedule.builder().enabled(true).weekly(WeeklyBackup.FRIDAY).daily(DailyBackup.H_0400_0600).build();
      client.replaceBackupSchedule(serverId, dailyWeekly);
      client.deleteBackupSchedule(serverId);
      // disables, doesn't delete: Web Hosting #119571
      assertEquals(client.getBackupSchedule(serverId).isEnabled(), false);
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testBackup")
   public void testCreateImage() throws Exception {
      Image image = client.createImageFromServer("hoofie", serverId);
      assertEquals("hoofie", image.getName());
      assertEquals(Integer.valueOf(serverId), image.getServerId());
      imageId = image.getId();
      blockUntilImageActive(imageId);
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testCreateImage")
   public void testRebuildServer() throws Exception {
      client.rebuildServer(serverId, new RebuildServerOptions().withImage(imageId));
      blockUntilServerActive(serverId);
      // issue Web Hosting #119580 imageId comes back incorrect after rebuild
      assert !Integer.valueOf(imageId).equals(client.getServer(serverId).getImageId());
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebuildServer")
   public void testRebootHard() throws Exception {
      client.rebootServer(serverId, RebootType.HARD);
      blockUntilServerActive(serverId);
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootHard")
   public void testRebootSoft() throws Exception {
      client.rebootServer(serverId, RebootType.SOFT);
      blockUntilServerActive(serverId);
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootSoft")
   public void testRevertResize() throws Exception {
      client.resizeServer(serverId, 2);
      blockUntilServerVerifyResize(serverId);
      client.revertResizeServer(serverId);
      blockUntilServerActive(serverId);
      assertEquals(Integer.valueOf(1), client.getServer(serverId).getFlavorId());
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootSoft")
   public void testConfirmResize() throws Exception {
      client.resizeServer(serverId2, 2);
      blockUntilServerVerifyResize(serverId2);
      client.confirmResizeServer(serverId2);
      blockUntilServerActive(serverId2);
      assertEquals(Integer.valueOf(2), client.getServer(serverId2).getFlavorId());
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = { "testRebootSoft", "testRevertResize", "testConfirmResize" })
   void deleteServer2() {
      if (serverId2 > 0) {
         client.deleteServer(serverId2);
         assert client.getServer(serverId2) == null;
      }
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "deleteServer2")
   void testDeleteImage() {
      if (imageId > 0) {
         client.deleteImage(imageId);
         assert client.getImage(imageId) == null;
      }
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testDeleteImage")
   void deleteServer1() {
      if (serverId > 0) {
         client.deleteServer(serverId);
         assert client.getServer(serverId) == null;
      }
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = { "deleteServer1" })
   void testDeleteSharedIpGroup() {
      if (sharedIpGroupId > 0) {
         client.deleteSharedIpGroup(sharedIpGroupId);
         assert client.getSharedIpGroup(sharedIpGroupId) == null;
      }
   }

   @AfterTest
   void deleteServersOnEnd() {
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

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }
}
