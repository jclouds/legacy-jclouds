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

package org.jclouds.openstack.nova;

import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.openstack.nova.options.ListOptions.Builder.withDetails;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.ImageStatus;
import org.jclouds.openstack.nova.domain.RebootType;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;
import org.jclouds.openstack.nova.options.RebuildServerOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NovaClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NovaClientLiveTest")
public class NovaClientLiveTest extends BaseComputeServiceContextLiveTest {

   public NovaClientLiveTest() {
      provider = "nova";
   }

   protected NovaClient client;
   protected SshClient.Factory sshFactory;
   protected Predicate<IPSocket> socketTester;

   @BeforeGroups(groups = { "integration", "live" })
   @Override
   public void setupContext() {
      super.setupContext();
      Injector injector = context.utils().injector();
      client = injector.getInstance(NovaClient.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 120, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
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

   // Server name must start with one or more alphabet and/or space and/or number. E.g. server 10,
   // server, server1
   private String serverPrefix = System.getProperty("user.name").replace('.', ' ');
   private int serverId;
   private String adminPass;
   Map<String, String> metadata = ImmutableMap.of("jclouds", "nova");
   private int serverId2;
   private String createdImageRef;

   public void testCreateServer() throws Exception {
      String flavorId = "1";
      Server server = null;
      while (server == null) {
         String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
         try {
            server = client.createServer(serverName, imageId, flavorId, withFile("/etc/jclouds.txt",
                     "nova".getBytes()).withMetadata(metadata));
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

   private void blockUntilImageActive(String imageRef) throws InterruptedException {
      Image currentDetails = null;
      for (currentDetails = client.getImage(imageRef); currentDetails.getStatus() != ImageStatus.ACTIVE; currentDetails = client
               .getImage(imageRef)) {
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
      assertEquals(new Integer(14362), server.getImage());
      assertEquals(new Integer(1), server.getFlavor());
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
      IPSocket socket = new IPSocket(Iterables.get(newDetails.getAddresses().getPublicAddresses(), 0).getAddress(), 22);
      socketTester.apply(socket);

      SshClient client = sshFactory.create(socket, LoginCredentials.builder().user("root").password(pass).build());
      try {
         client.connect();
         Payload etcPasswd = client.get("/etc/jclouds.txt");
         String etcPasswdContents = Strings2.toStringAndClose(etcPasswd.getInput());
         assertEquals("nova", etcPasswdContents.trim());
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

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testChangePassword")
   public void testCreateImage() throws Exception {
      Image image = client.createImageFromServer("hoofie", serverId);
      assertEquals("hoofie", image.getName());
      assertEquals(new Integer(serverId), image.getServerRef());
      createdImageRef = image.getId()+"";
      blockUntilImageActive(createdImageRef);
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testCreateImage")
   public void testRebuildServer() throws Exception {
      client.rebuildServer(serverId, new RebuildServerOptions().withImage(createdImageRef));
      blockUntilServerActive(serverId);
      assertEquals(new Integer(createdImageRef).intValue(),client.getServer(serverId).getImage().getId());
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
      assertEquals(new Integer(1), client.getServer(serverId).getFlavorRef());
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootSoft")
   public void testConfirmResize() throws Exception {
      client.resizeServer(serverId2, 2);
      blockUntilServerVerifyResize(serverId2);
      client.confirmResizeServer(serverId2);
      blockUntilServerActive(serverId2);
      assertEquals(new Integer(2), client.getServer(serverId2).getFlavorRef());
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
      if (createdImageRef != null) {
         client.deleteImage(createdImageRef);
         assert client.getImage(createdImageRef) == null;
      }
   }

   @Test(timeOut = 10 * 60 * 1000, dependsOnMethods = "testDeleteImage")
   void deleteServer1() {
      if (serverId > 0) {
         client.deleteServer(serverId);
         assert client.getServer(serverId) == null;
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
      if (createdImageRef != null) {
         client.deleteImage(createdImageRef);
         assert client.getImage(createdImageRef) == null;
      }
   }
}
