/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.openstack.nova;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payload;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.openstack.nova.domain.*;
import org.jclouds.openstack.nova.options.RebuildServerOptions;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.openstack.nova.PropertyHelper.overridePropertyFromSystemProperty;
import static org.jclouds.openstack.nova.options.CreateServerOptions.Builder.withFile;
import static org.jclouds.openstack.nova.options.ListOptions.Builder.withDetails;
import static org.testng.Assert.*;

/**
 * Tests behavior of {@code NovaClient}
 *
 * @author Adrian Cole
 */
// disabled [Web Hosting #129069
@Test(groups = "live", sequential = true)
public class NovaClientLiveTest {

   protected NovaClient client;
   protected SshClient.Factory sshFactory;
   private Predicate<IPSocket> socketTester;
   protected String provider = "nova";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   private String serverPrefix = System.getProperty("user.name") + ".cs";
   private int serverId;
   private String adminPass;
   Map<String, String> metadata = ImmutableMap.of("jclouds", "rackspace");
   private String ip;
   private int serverId2;
   private String adminPass2;
   private int imageId;


   protected Properties setupProperties() throws IOException {
      Properties overrides = new Properties();
      overrides.load(this.getClass().getResourceAsStream("/test.properties"));
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".endpoint");
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".apiversion");
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".identity");
      overridePropertyFromSystemProperty(overrides, "test." + provider + ".credential");
      overridePropertyFromSystemProperty(overrides, "test.initializer");
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");

      return overrides;
   }

   protected void setupCredentials(Properties properties) {
      identity = checkNotNull(properties.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(properties.getProperty("test." + provider + ".credential"), "test." + provider
            + ".credential");
      endpoint = properties.getProperty("test." + provider + ".endpoint");
      apiversion = properties.getProperty("test." + provider + ".apiversion");
   }

   protected void updateProperties(final Properties properties) {
      properties.setProperty(provider + ".identity", identity);
      properties.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         properties.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         properties.setProperty(provider + ".apiversion", apiversion);
   }


   @BeforeGroups(groups = {"live"})
   public void setupClient() throws IOException {
      Properties overrides = setupProperties();
      setupCredentials(overrides);
      updateProperties(overrides);

      String identity = "admin";
      String credential = "d744752f-20d3-4d75-979f-f62f16033b07";
//       ComputeServiceContextFactory contextFactory = new ComputeServiceContextFactory();
//       ComputeServiceContext context = contextFactory.createContext(provider, identity, credential, Collections.singleton(new JschSshClientModule()), overrides);

      Injector injector = new RestContextFactory().createContextBuilder(provider, identity, credential,
            ImmutableSet.<Module>of(new SLF4JLoggingModule(), new JschSshClientModule()), overrides)
            .buildInjector();

      client = injector.getInstance(NovaClient.class);

      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 120, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

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
      String imageRef = client.getImage(13).getURI().toASCIIString();
      String flavorRef = client.getFlavor(1).getURI().toASCIIString();
      String serverName = serverPrefix + "createserver" + new SecureRandom().nextInt();
      Server server = client.createServer(serverName, imageRef, flavorRef, withFile("/etc/jclouds.txt",
            "rackspace".getBytes()).withMetadata(metadata));

      assertNotNull(server.getAdminPass());
      assertEquals(server.getStatus(), ServerStatus.BUILD);
      serverId = server.getId();
      adminPass = server.getAdminPass();
      blockUntilServerActive(serverId);
      ip = client.getServer(serverId).getAddresses().getPublicAddresses().iterator().next();
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

   private void blockUntilImageActive(int imageId) throws InterruptedException {
      Image currentDetails;
      for (currentDetails = client.getImage(imageId); currentDetails.getStatus() != ImageStatus.ACTIVE; currentDetails = client
            .getImage(imageId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   @Test(enabled = true, timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testServerDetails() throws Exception {
      Server server = client.getServer(serverId);

      assertNotNull(server.getHostId());
      assertEquals(server.getStatus(), ServerStatus.ACTIVE);


      assertNotNull(server.getAddresses());


      // check metadata
      assertEquals(server.getMetadata(), metadata);
      assertPassword(server, adminPass);
      assertEquals(server.getFlavorRef(), endpoint + "/flavors/1");
      assert server.getProgress() >= 0 : "newDetails.getProgress()" + server.getProgress();
      assertEquals(server.getImageRef(), endpoint + "/images/13");
      // listAddresses tests..
      assertEquals(client.getAddresses(serverId), server.getAddresses());
      assertEquals(server.getAddresses().getPublicAddresses().size(), 1);
      assertEquals(client.listPublicAddresses(serverId), server.getAddresses().getPublicAddresses());
      assertEquals(server.getAddresses().getPrivateAddresses().size(), 1);
      assertEquals(client.listPrivateAddresses(serverId), server.getAddresses().getPrivateAddresses());

   }


   private void assertPassword(Server server, String pass) throws IOException {
      IPSocket socket = new IPSocket(Iterables.get(server.getAddresses().getPublicAddresses(), 0), 22);
      socketTester.apply(socket);

      SshClient client = sshFactory.create(socket, new Credentials("root", pass));
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

   private ExecResponse exec(Server details, String pass, String command) throws IOException {
      IPSocket socket = new IPSocket(Iterables.get(details.getAddresses().getPublicAddresses(), 0), 22);
      socketTester.apply(socket);
      SshClient client = sshFactory.create(socket, new Credentials("root", pass));
      try {
         client.connect();
         return client.exec(command);
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   @Test(enabled = false, timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testRenameServer() throws Exception {
      Server server = client.getServer(serverId);
      String oldName = server.getName();
      client.renameServer(serverId, oldName + "new");
      blockUntilServerActive(serverId);
      assertEquals(oldName + "new", client.getServer(serverId).getName());
   }

   @Test(enabled = false, timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateServer")
   public void testChangePassword() throws Exception {
      client.changeAdminPass(serverId, "elmo");
      blockUntilServerActive(serverId);
      assertPassword(client.getServer(serverId), "elmo");
      this.adminPass = "elmo";
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

   private void assertIpNotConfigured(Server server, String password) throws IOException {
      ExecResponse response = exec(server, password, "ifconfig -a");
      assert response.getOutput().indexOf(ip) == -1 : String.format("server %s still has get ip %s%n%s", server, ip,
            response);
   }


   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testBackup")
   public void testCreateImage() throws Exception {
      Image image = client.createImageFromServer("hoofie", serverId);
      assertEquals("hoofie", image.getName());
      assertEquals(new Integer(serverId), image.getServerId());
      imageId = image.getId();
      blockUntilImageActive(imageId);
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testCreateImage")
   public void testRebuildServer() throws Exception {
      client.rebuildServer(serverId, new RebuildServerOptions().withImage(imageId));
      blockUntilServerActive(serverId);
      // issue Web Hosting #119580 imageId comes back incorrect after rebuild
      assertEquals(imageId, client.getServer(serverId).getImageRef());
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebuildServer")
   public void testRebootHard() throws Exception {
      client.rebootServer(serverId, RebootType.HARD);
      blockUntilServerActive(serverId);
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootHard")
   public void testRebootSoft() throws Exception {
      client.rebootServer(serverId, RebootType.SOFT);
      blockUntilServerActive(serverId);
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootSoft")
   public void testRevertResize() throws Exception {
      client.resizeServer(serverId, 2);
      blockUntilServerVerifyResize(serverId);
      client.revertResizeServer(serverId);
      blockUntilServerActive(serverId);
      assertEquals(new Integer(1), client.getServer(serverId).getFlavorRef());
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootSoft")
   public void testConfirmResize() throws Exception {
      client.resizeServer(serverId2, 2);
      blockUntilServerVerifyResize(serverId2);
      client.confirmResizeServer(serverId2);
      blockUntilServerActive(serverId2);
      assertEquals(new Integer(2), client.getServer(serverId2).getFlavorRef());
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = {"testRebootSoft", "testRevertResize",
         "testConfirmResize"})
   void deleteServer2() {
      if (serverId2 > 0) {
         client.deleteServer(serverId2);
         assert client.getServer(serverId2) == null;
      }
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "deleteServer2")
   void testDeleteImage() {
      if (imageId > 0) {
         client.deleteImage(imageId);
         assert client.getImage(imageId) == null;
      }
   }

   @Test(enabled = false, timeOut = 10 * 60 * 1000, dependsOnMethods = "testDeleteImage")
   void deleteServer1() {
      if (serverId > 0) {
         client.deleteServer(serverId);
         assert client.getServer(serverId) == null;
      }
   }

   @AfterTest
   void deleteServersOnEnd() {
      if (serverId > 0) {
         //client.deleteServer(serverId);
      }
      if (serverId2 > 0) {
         client.deleteServer(serverId2);
      }
   }
}
