/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.openstack.nova.live.novaclient;

import com.google.common.collect.Iterables;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.domain.*;
import org.jclouds.openstack.nova.options.RebuildServerOptions;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Set;

import static org.jclouds.openstack.nova.options.ListOptions.Builder.withDetails;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code NovaClient}
 *
 * @author Adrian Cole
 */
// disabled [Web Hosting #129069
@Test(groups = "live", sequential = true)
public class NovaClientLiveTest extends ClientBase {


   @Test
   public void testListServers() throws Exception {
      Set<Server> response = client.listServers();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
   }

   @Test
   public void testListServersDetail() throws Exception {
      Set<Server> response = client.listServers(withDetails());
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);
   }

   @Test
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

   @Test
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

   @Test
   public void testGetImagesDetail() throws Exception {
      Set<Image> response = client.listImages(withDetails());
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         try {
            Image newDetails = client.getImage(image.getId());
            assertEquals(image, newDetails);
         } catch (HttpResponseException e) {// Ticket #9867
            if (e.getResponse().getStatusCode() != 400)
               throw e;
         }
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

   @Test
   public void testGetServersDetail() throws Exception {
      Set<Server> response = client.listServers(withDetails());
      assert null != response;
      assertTrue(response.size() >= 0);
      for (Server server : response) {
         Server newDetails = client.getServer(server.getId());
         System.out.println("====");

         System.out.println(server);
         System.out.println(newDetails);
         System.out.println("====");
      }
      for (Server server : response) {
         Server newDetails = client.getServer(server.getId());
         assertEquals(server, newDetails);
      }
   }

   @Test
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

   @Test
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

   @Test
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


   @Test(enabled = true)
   public void testCreateServer() throws Exception {
      Server server = getDefaultServerImmediately();
      assertNotNull(server.getAdminPass());
      assertEquals(server.getStatus(), ServerStatus.BUILD);
      int serverId = server.getId();
      String adminPass = server.getAdminPass();
      blockUntilServerActive(serverId);
      blockUntilPublicAddress(serverId);
      client.getServer(serverId).getAddresses().getPublicAddresses().iterator().next().getAddress();
   }

   private void blockUntilPublicAddress(int serverId) throws InterruptedException {
      while (client.getServer(serverId).getAddresses().getPublicAddresses().isEmpty()) {
         System.out.println("Awaiting public address");
         Thread.sleep(1000);
      }
   }

   private void blockUntilServerActive(int serverId) throws InterruptedException {
      Server currentDetails;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.ACTIVE; currentDetails = client
            .getServer(serverId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   private void blockUntilServerVerifyResize(int serverId) throws InterruptedException {
      Server currentDetails;
      for (currentDetails = client.getServer(serverId); currentDetails.getStatus() != ServerStatus.VERIFY_RESIZE; currentDetails = client
            .getServer(serverId)) {
         System.out.printf("blocking on status verify resize%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   private void blockUntilImageActive(int createdImageId) throws InterruptedException {
      Image currentDetails;
      for (currentDetails = client.getImage(createdImageId); currentDetails.getStatus() != ImageStatus.ACTIVE; currentDetails = client
            .getImage(createdImageId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   @Test(enabled = true, timeOut = 300000)
   public void testServerDetails() throws Exception {
      Server server = getDefaultServerImmediately();
      assertNotNull(server.getHostId(), "Host id: ");
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);
      assertNotNull(server.getAddresses());
      // check metadata
      assertEquals(server.getMetadata(), metadata);
      assertTrue(server.getImageRef().endsWith(String.valueOf(testImageId)));
      // listAddresses tests..
      assertEquals(client.getAddresses(server.getId()), server.getAddresses());
      assertEquals(server.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(client.listPublicAddresses(server.getId()), server.getAddresses().getPublicAddresses());
      assertEquals(server.getAddresses().getPrivateAddresses().size(), 1);
      assertEquals(client.listPrivateAddresses(server.getId()), server.getAddresses().getPrivateAddresses());
      assertPassword(server, server.getAdminPass());
      assertTrue(server.getFlavorRef().endsWith("1"));
      assert server.getProgress() >= 0 : "newDetails.getProgress()" + server.getProgress();
   }


   private void assertPassword(Server server, String pass) throws IOException {
      IPSocket socket = new IPSocket(Iterables.get(server.getAddresses().getPublicAddresses(), 0).getAddress(), 22);
      //socketTester.apply(socket);

      SshClient client = sshFactory.create(socket, new Credentials("root", keyPair.get("private")));
      try {
         client.connect();
         Payload etcPasswd = client.get("/etc/jclouds.txt");
         String etcPasswdContents = Strings2.toStringAndClose(etcPasswd.getInput());
         assertEquals("rackspace", etcPasswdContents.trim());
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @Test(enabled = true, timeOut = 5 * 60 * 1000)
   public void testRenameServer() throws Exception {
      Server server = getDefaultServerImmediately();
      int serverId = server.getId();
      String oldName = server.getName();
      client.renameServer(serverId, oldName + "new");
      blockUntilServerActive(serverId);
      assertEquals(oldName + "new", client.getServer(serverId).getName());
   }

   @Test(enabled = true, timeOut = 5 * 60 * 1000)
   public void testChangePassword() throws Exception {
      int serverId = getDefaultServerImmediately().getId();
      blockUntilServerActive(serverId);
      client.changeAdminPass(serverId, "elmo");
      assertPassword(client.getServer(serverId), "elmo");

   }

   @Test(enabled = true, timeOut = 10 * 600 * 1000)
   public void testCreateImage() throws Exception {
      Server server = getDefaultServerImmediately();
      Image image = getDefaultImageImmediately(server);
      blockUntilImageActive(image.getId());
      assertEquals("hoofie", image.getName());
      assertEquals(image.getServerRef(), "");
   }


   @Test(enabled = true, timeOut = 10 * 60 * 1000)
   public void testRebuildServer() throws Exception {
      Server server = getDefaultServerImmediately();
      Image image = getDefaultImageImmediately(server);
      client.rebuildServer(server.getId(), new RebuildServerOptions().withImage(String.valueOf(image.getId())));
      blockUntilServerActive(server.getId());
      // issue Web Hosting #119580 createdImageId comes back incorrect after rebuild
      assertEquals(image.getURI(), client.getServer(server.getId()).getImageRef());
   }

   @Test(enabled = true, timeOut = 10 * 60 * 1000)
   public void testRebootHard() throws Exception {
      Server server = getDefaultServerImmediately();
      client.rebootServer(server.getId(), RebootType.HARD);
      blockUntilServerActive(server.getId());
      //TODO check
   }

   @Test(enabled = true, timeOut = 10 * 60 * 1000)
   public void testRebootSoft() throws Exception {
      Server server = getDefaultServerImmediately();
      client.rebootServer(server.getId(), RebootType.SOFT);
      blockUntilServerActive(server.getId());
      //TODO check
   }

   @Test(enabled = false, timeOut = 60000, dependsOnMethods = "testRebootSoft")
   public void testRevertResize() throws Exception {
      Server server = getDefaultServerImmediately();
      int serverId = server.getId();
      client.resizeServer(serverId, 2);
      blockUntilServerVerifyResize(serverId);
      client.revertResizeServer(serverId);
      blockUntilServerActive(serverId);
      assertEquals(1, client.getServer(serverId).getFlavorRef());
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000)
   public void testConfirmResize() throws Exception {
      Server server = getDefaultServerImmediately();
      int serverId = server.getId();
      client.resizeServer(serverId, 2);
      blockUntilServerVerifyResize(serverId);
      client.confirmResizeServer(serverId);
      blockUntilServerActive(serverId);
      assertEquals(2, client.getServer(serverId).getFlavorRef());
   }

   @Test(enabled = true, timeOut = 60000)
   void deleteServer2() throws Exception {
      Server server = getDefaultServerImmediately();
      int serverId = server.getId();
      client.deleteServer(serverId);
      waitServerDeleted(serverId);
   }

   @Test(enabled = true, timeOut = 60000)
   void testDeleteImage() throws Exception {
      Image image = getDefaultImageImmediately(getDefaultServerImmediately());
      client.deleteImage(image.getId());
      assert client.getImage(image.getId()) == null;
   }

   @Test(enabled = true, timeOut = 60000)
   void deleteServer1() throws Exception {
      Server server = getDefaultServerImmediately();
      int serverId = server.getId();
      client.deleteServer(serverId);
      waitServerDeleted(serverId);
   }

   @Test
   public void testDeleteAllCreatedServers() {
      for (Server server : client.listServers()) {
         if (server.getName().startsWith(serverPrefix)) {
            client.deleteServer(server.getId());
            System.out.println("Deleted server: " + server);
         }
      }
   }

   @AfterTest
   void deleteServersOnEnd() {
      testDeleteAllCreatedServers();
   }

}
