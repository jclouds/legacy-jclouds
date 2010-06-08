/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibmdev;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.ibmdev.options.CreateInstanceOptions.Builder.attachIp;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.http.HttpResponseException;
import org.jclouds.ibmdev.domain.Address;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.domain.Key;
import org.jclouds.ibmdev.domain.Location;
import org.jclouds.ibmdev.domain.Volume;
import org.jclouds.ibmdev.domain.Instance.Software;
import org.jclouds.ibmdev.predicates.AddressFree;
import org.jclouds.ibmdev.predicates.VolumeUnmounted;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

/**
 * Tests behavior of {@code IBMDeveloperCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ibmdevelopercloud.IBMDeveloperCloudClientLiveTest")
public class IBMDeveloperCloudClientLiveTest {

   private IBMDeveloperCloudClient connection;
   private Location location;
   private Address ip;
   private ImmutableMap<String, String> keyPair;
   private Key key;
   private Volume volume;
   private Instance instance;
   private String user;

   private static final String TAG = System.getProperty("user.name");

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      connection = (IBMDeveloperCloudClient) IBMDeveloperCloudContextFactory.createContext(user,
               password, new Log4JLoggingModule()).getProviderSpecificContext().getApi();
   }

   @Test
   public void testListImages() throws Exception {
      Set<? extends Image> response = connection.listImages();
      assertNotNull(response);
   }

   @Test
   public void testGetImage() throws Exception {
      Set<? extends Image> response = connection.listImages();
      assertNotNull(response);
      if (response.size() > 0) {
         Image image = Iterables.get(response, 0);
         assertEquals(connection.getImage(image.getId()).getId(), image.getId());
      }
   }

   @Test
   public void testListInstances() throws Exception {
      Set<? extends Instance> response = connection.listInstances();
      assertNotNull(response);
   }

   @Test
   public void testListInstancesFromRequestReturnsNull() throws Exception {
      Set<? extends Instance> response = connection.listInstancesFromRequest(Long.MAX_VALUE + "");
      assertNull(response);
   }

   @Test
   public void testGetInstance() throws Exception {
      Set<? extends Instance> response = connection.listInstances();
      assertNotNull(response);
      if (response.size() > 0) {
         Instance instance = Iterables.get(response, 0);
         assertEquals(connection.getInstance(instance.getId()).getId(), instance.getId());
      }
   }

   @Test
   public void testListKeys() throws Exception {
      Set<? extends Key> response = connection.listKeys();
      assertNotNull(response);
   }

   @Test
   public void testGetKey() throws Exception {
      Set<? extends Key> response = connection.listKeys();
      assertNotNull(response);
      if (response.size() > 0) {
         Key key = Iterables.get(response, 0);
         assertEquals(connection.getKey(key.getName()).getName(), key.getName());
      }
   }

   @Test
   public void testListVolumes() throws Exception {
      Set<? extends Volume> response = connection.listVolumes();
      assertNotNull(response);
   }

   @Test
   public void testGetVolume() throws Exception {
      Set<? extends Volume> response = connection.listVolumes();
      assertNotNull(response);
      if (response.size() > 0) {
         Volume image = Iterables.get(response, 0);
         assertEquals(connection.getVolume(image.getId()).getId(), image.getId());
      }
   }

   @Test
   public void testListLocations() throws Exception {
      Set<? extends Location> response = connection.listLocations();
      assertNotNull(response);
   }

   @Test
   public void testGetLocation() throws Exception {
      Set<? extends Location> response = connection.listLocations();
      assertNotNull(response);
      if (response.size() > 0) {
         location = Iterables.get(response, 0);
         assertEquals(connection.getLocation(location.getId()).getId(), location.getId());
      }
   }

   @Test
   public void testListAddresss() throws Exception {
      Set<? extends Address> response = connection.listAddresses();
      assertNotNull(response);
   }

   @Test(dependsOnMethods = "testGetLocation")
   public void testAddPublicKey() throws Exception {
      try {
         connection.addPublicKey(TAG, keyPair.get("public"));
      } catch (IllegalStateException e) {
         // must not have been found
         connection.updatePublicKey(TAG, keyPair.get("public"));
      }
      key = connection.getKey(TAG);
      assertEquals(key.getName(), TAG);
      assertEquals(key.getInstanceIds(), ImmutableSet.<Long> of());
      assert keyPair.get("public").indexOf(key.getKeyMaterial()) > 0;
      assertNotNull(key.getLastModifiedTime());
   }

   @Test(dependsOnMethods = "testGetLocation")
   public void testAllocateIpAddress() throws Exception {
      try {
         ip = connection.allocateAddressInLocation(location.getId());
         assertEquals(ip.getIp(), null);
         // wait up to 30 seconds for this to become "free"
         assert new RetryablePredicate<Address>(new AddressFree(connection), 30, 2,
                  TimeUnit.SECONDS).apply(ip);
      } catch (IllegalStateException e) {
         if (HttpResponseException.class.cast(e.getCause()).getResponse().getStatusCode() == 409) {
            ip = Iterables.find(connection.listAddresses(), new Predicate<Address>() {

               @Override
               public boolean apply(Address input) {
                  return input.getState() == Address.State.FREE;
               }

            });
         } else {
            throw e;
         }
      }
      assertEquals(ip.getInstanceId(), "0");
      assertEquals(ip.getLocation(), location.getId());

      final String id = ip.getId();

      Set<? extends Address> allAddresses = connection.listAddresses();

      // refresh address as it may have been just created
      ip = Iterables.find(allAddresses, new Predicate<Address>() {

         @Override
         public boolean apply(Address input) {
            return input.getId().equals(id);
         }

      });

      assert (allAddresses.contains(ip)) : String.format("ip %s not in %s", ip, allAddresses);
   }

   @Test(dependsOnMethods = "testGetLocation")
   public void testCreateVolume() throws Exception {
      try {
         volume = connection.createVolumeInLocation(location.getId(), TAG, "EXT3", "SMALL");
         // wait up to 5 minutes for this to become "unmounted"
         assert new RetryablePredicate<Volume>(new VolumeUnmounted(connection), 300, 5,
                  TimeUnit.SECONDS).apply(volume);
      } catch (IllegalStateException e) {
         if (HttpResponseException.class.cast(e.getCause()).getResponse().getStatusCode() == 409) {
            volume = Iterables.find(connection.listVolumes(), new Predicate<Volume>() {

               @Override
               public boolean apply(Volume input) {
                  return input.getState() == Volume.State.UNMOUNTED;
               }

            });
         } else {
            throw e;
         }
      }
      assertEquals(volume.getInstanceId(), "0");
      assertEquals(volume.getLocation(), location.getId());

      final String id = volume.getId();
      Set<? extends Volume> allVolumes = connection.listVolumes();

      // refresh volume as it may have been just created
      volume = Iterables.find(allVolumes, new Predicate<Volume>() {

         @Override
         public boolean apply(Volume input) {
            return input.getId().equals(id);
         }

      });

      assert (allVolumes.contains(volume)) : String.format("volume %s not in %s", volume, volume);
   }

   private static final String IMAGE_ID = "11";// Rational Insight

   /**
    * cannot run an instance due to 500 errors:
    * 
    * http://www-180.ibm.com/cloud/enterprise/beta/ram/community/discussionTopic.faces?guid={
    * DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&fid=1068&tid=1523#topic
    */
   @Test(expectedExceptions = HttpResponseException.class, dependsOnMethods = { "testAddPublicKey",
            "testAllocateIpAddress", "testCreateVolume" })
   public void testCreateInstanceWithOptions() throws Exception {
      instance = connection.createInstanceInLocation(location.getId(), TAG, IMAGE_ID, "LARGE",
               attachIp(ip.getId()).authorizePublicKey(key.getName()).mountVolume(volume.getId(),
                        "/mnt").configurationData(
                        ImmutableMap.of("insight_admin_password", "myPassword1",
                                 "db2_admin_password", "myPassword2", "report_user_password",
                                 "myPassword3")));
      assertEquals(instance.getLocation(), location.getId());
      assertNotNull(instance.getHostname());
      assertEquals(instance.getIp(), ip.getIp());
      assertEquals(instance.getKeyName(), key.getName());
      assertNotNull(instance.getExpirationTime());
      assertNotNull(instance.getLaunchTime());
      assertEquals(instance.getInstanceType(), "SMALL");
      assertNotNull(instance.getName());
      assertEquals(instance.getOwner(), user);
      assertEquals(instance.getImageId(), IMAGE_ID);
      assertEquals(instance.getSoftware(), ImmutableSet.<Software> of());
      assertEquals(instance.getProductCodes(), ImmutableSet.<String> of());
      assertEquals(instance.getStatus(), Instance.Status.NEW);
      assertNotNull(instance.getRequestName());

      volume = connection.getVolume(volume.getId());
      assertEquals(volume.getInstanceId(), instance.getId());

      refreshIp();
      assertEquals(ip.getInstanceId(), ip.getId());

   }

   private void refreshIp() {
      Set<? extends Address> allAddresses = connection.listAddresses();
      final String id = ip.getId();
      // refresh address as it may have been just created
      ip = Iterables.find(allAddresses, new Predicate<Address>() {

         @Override
         public boolean apply(Address input) {
            return input.getId().equals(id);
         }

      });
   }

   @AfterTest(groups = { "live" })
   void tearDown() {
      if (volume != null)
         try {
            connection.deleteVolume(volume.getId());
         } catch (Exception e) {

         }
      if (ip != null)
         try {
            connection.releaseAddress(ip.getId());
         } catch (Exception e) {

         }
      if (key != null)
         try {
            connection.deleteKey(key.getName());
         } catch (Exception e) {

         }
      if (instance != null)
         try {
            connection.deleteInstance(instance.getId());
         } catch (Exception e) {

         }
   }

   @BeforeGroups(groups = { "live" })
   protected void setupKeyPair() throws FileNotFoundException, IOException {
      String secretKeyFile;
      try {
         secretKeyFile = checkNotNull(System.getProperty("jclouds.test.ssh.keyfile"),
                  "jclouds.test.ssh.keyfile");
      } catch (NullPointerException e) {
         secretKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
      }
      String secret = Files.toString(new File(secretKeyFile), Charsets.UTF_8);
      assert secret.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + secret;
      keyPair = ImmutableMap.<String, String> of("private", secret, "public", Files.toString(
               new File(secretKeyFile + ".pub"), Charsets.UTF_8));
   }
}
