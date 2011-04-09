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
package org.jclouds.slicehost;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.io.Payloads;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.slicehost.domain.Flavor;
import org.jclouds.slicehost.domain.Image;
import org.jclouds.slicehost.domain.Slice;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code SlicehostClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class SlicehostClientLiveTest {

   protected SlicehostClient client;
   protected SshClient.Factory sshFactory;
   private Predicate<IPSocket> socketTester;

   protected String provider = "slicehost";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      endpoint = checkNotNull(System.getProperty("test." + provider + ".endpoint"), "test." + provider + ".endpoint");
      apiversion = checkNotNull(System.getProperty("test." + provider + ".apiversion"), "test." + provider
            + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".endpoint", endpoint);
      overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      Injector injector = new RestContextFactory().createContextBuilder(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), overrides).buildInjector();

      client = injector.getInstance(SlicehostClient.class);
      sshFactory = injector.getInstance(SshClient.Factory.class);
      SocketOpen socketOpen = injector.getInstance(SocketOpen.class);
      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 120, 1, TimeUnit.SECONDS);
      injector.injectMembers(socketOpen); // add logger
   }

   public void testListSlices() throws Exception {

      Set<Slice> response = client.listSlices();
      assert null != response;
      long initialContainerCount = response.size();
      assertTrue(initialContainerCount >= 0);

   }

   public void testListSlicesDetail() throws Exception {
      Set<Slice> response = client.listSlices();
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
      Set<Image> response = client.listImages();
      assert null != response;
      long imageCount = response.size();
      assertTrue(imageCount >= 0);
      for (Image image : response) {
         assertTrue(image.getId() >= 1);
         assert null != image.getName() : image;
      }
   }

   public void testGetImagesDetail() throws Exception {
      Set<Image> response = client.listImages();
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
   public void testGetSliceDetailsNotFound() throws Exception {
      assert client.getSlice(12312987) == null;
   }

   public void testGetSlicesDetail() throws Exception {
      Set<Slice> response = client.listSlices();
      assert null != response;
      long sliceCount = response.size();
      assertTrue(sliceCount >= 0);
      for (Slice slice : response) {
         Slice newDetails = client.getSlice(slice.getId());
         assertEquals(slice, newDetails);
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
      Set<Flavor> response = client.listFlavors();
      assert null != response;
      long flavorCount = response.size();
      assertTrue(flavorCount >= 0);
      for (Flavor flavor : response) {
         assertTrue(flavor.getId() >= 1);
         assert null != flavor.getName() : flavor;
         assert -1 != flavor.getRam() : flavor;
      }
   }

   public void testGetFlavorsDetail() throws Exception {
      Set<Flavor> response = client.listFlavors();
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

   private String slicePrefix = System.getProperty("user.name") + ".sh";
   private int sliceId;
   private String rootPassword;

   @Test(enabled = true)
   public void testCreateSlice() throws Exception {
      int imageId = 14362;
      int flavorId = 1;
      Slice slice = null;
      while (slice == null) {
         String sliceName = slicePrefix + "createslice" + new SecureRandom().nextInt();
         try {
            slice = client.createSlice(sliceName, imageId, flavorId);
         } catch (UndeclaredThrowableException e) {
            HttpResponseException htpe = (HttpResponseException) e.getCause().getCause();
            if (htpe.getResponse().getStatusCode() == 400)
               continue;
            throw e;
         }
      }
      assertNotNull(slice.getRootPassword());
      sliceId = slice.getId();
      rootPassword = slice.getRootPassword();
      assertEquals(slice.getStatus(), Slice.Status.BUILD);
      blockUntilSliceActive(sliceId);
   }

   private void blockUntilSliceActive(int sliceId) throws InterruptedException {
      Slice currentDetails = null;
      for (currentDetails = client.getSlice(sliceId); currentDetails.getStatus() != Slice.Status.ACTIVE; currentDetails = client
            .getSlice(sliceId)) {
         System.out.printf("blocking on status active%n%s%n", currentDetails);
         Thread.sleep(5 * 1000);
      }
   }

   @Test(enabled = true, timeOut = 5 * 60 * 1000, dependsOnMethods = "testCreateSlice")
   public void testSliceDetails() throws Exception {
      Slice slice = client.getSlice(sliceId);
      assertEquals(slice.getStatus(), Slice.Status.ACTIVE);
      assert slice.getProgress() >= 0 : "newDetails.getProgress()" + slice.getProgress();
      assertEquals(new Integer(14362), slice.getImageId());
      assertEquals(1, slice.getFlavorId());
      assertNotNull(slice.getAddresses());
      checkPassOk(slice, rootPassword);
   }

   private void checkPassOk(Slice newDetails, String pass) throws IOException {
      try {
         doCreateMarkerFile(newDetails, pass);
      } catch (SshException e) {// try twice in case there is a network timeout
         try {
            Thread.sleep(10 * 1000);
         } catch (InterruptedException e1) {
         }
         doCreateMarkerFile(newDetails, pass);
      }
   }

   private void doCreateMarkerFile(Slice newDetails, String pass) throws IOException {
      String ip = getIp(newDetails);
      IPSocket socket = new IPSocket(ip, 22);
      socketTester.apply(socket);

      SshClient client = sshFactory.create(socket, new Credentials("root", pass));
      try {
         client.connect();
         client.put("/etc/jclouds.txt", Payloads.newStringPayload("slicehost"));
      } finally {
         if (client != null)
            client.disconnect();
      }
   }

   private String getIp(Slice newDetails) {
      String ip = Iterables.find(newDetails.getAddresses(), new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return !input.startsWith("10.");
         }

      });
      return ip;
   }

   @Test(enabled = true, timeOut = 10 * 60 * 1000, dependsOnMethods = "testSliceDetails")
   public void testRebootHard() throws Exception {
      client.hardRebootSlice(sliceId);
      blockUntilSliceActive(sliceId);
   }

   @Test(enabled = true, timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootHard")
   public void testRebootSoft() throws Exception {
      client.rebootSlice(sliceId);
      blockUntilSliceActive(sliceId);
   }

   @Test(enabled = true, timeOut = 10 * 60 * 1000, dependsOnMethods = "testRebootSoft")
   void destroySlice1() {
      if (sliceId > 0) {
         client.destroySlice(sliceId);
         assert client.getSlice(sliceId) == null;
      }
   }

   @AfterTest
   void destroySlicesOnEnd() {
      if (sliceId > 0) {
         client.destroySlice(sliceId);
      }

   }
}
