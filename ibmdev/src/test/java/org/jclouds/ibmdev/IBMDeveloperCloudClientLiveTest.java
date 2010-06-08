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
import static org.jclouds.ibmdev.options.CreateInstanceOptions.Builder.configurationData;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
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
import org.jclouds.ibmdev.predicates.InstanceActive;
import org.jclouds.ibmdev.predicates.InstanceRemovedOrNotFound;
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

   private static final ImmutableSet<Software> SOFTWARE = ImmutableSet.<Software> of(new Software(
            "SUSE Linux Enterprise", "OS", "10 SP2"));
   private static final String SIZE = "LARGE";
   private IBMDeveloperCloudClient connection;
   private Location location;
   private Address ip;
   private ImmutableMap<String, String> keyPair;
   private Key key;
   private Volume volume;
   private String user;
   private Instance instance2;
   private Instance instance;

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
         key = connection.getKey(TAG);
         try {
            assert key.getInstanceIds().equals(ImmutableSet.<String> of()) : key;
         } catch (AssertionError e) {
            // inconsistency in the key api when recreating a key
            // http://www-180.ibm.com/cloud/enterprise/beta/ram/community/discussionTopic.faces?guid={DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&fid=1068&tid=1528
         }
      } catch (IllegalStateException e) {
         // must have been found
         connection.updatePublicKey(TAG, keyPair.get("public"));
         key = connection.getKey(TAG);
         for (String instanceId : key.getInstanceIds()) {
            Instance instance = connection.getInstance(instanceId);
            System.out.println("deleting instance: " + instance);
            if (instance.getStatus() == Instance.Status.FAILED
                     || instance.getStatus() == Instance.Status.ACTIVE) {
               connection.deleteInstance(instanceId);
               assert new RetryablePredicate<Instance>(new InstanceRemovedOrNotFound(connection),
                        30, 2, TimeUnit.SECONDS).apply(instance) : instance;
            }
         }
      }
      assertEquals(key.getName(), TAG);
      assert keyPair.get("public").indexOf(key.getKeyMaterial()) > 0;
      assertNotNull(key.getLastModifiedTime());
   }

   @Test(dependsOnMethods = "testGetLocation")
   public void testAllocateIpAddress() throws Exception {
      try {
         ip = connection.allocateAddressInLocation(location.getId());
         assertEquals(ip.getIp(), null);
         // wait up to 30 seconds for this to become "free"
         new RetryablePredicate<Address>(new AddressFree(connection), 30, 2, TimeUnit.SECONDS)
                  .apply(ip);
         refreshIpAndReturnAllAddresses();
         assertEquals(ip.getInstanceId(), null);
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
      assertEquals(ip.getInstanceId(), null);
      assertEquals(ip.getLocation(), location.getId());

      Set<? extends Address> allAddresses = refreshIpAndReturnAllAddresses();

      assert (allAddresses.contains(ip)) : String.format("ip %s not in %s", ip, allAddresses);
   }

   @Test(enabled = false, dependsOnMethods = "testGetLocation")
   public void testCreateVolume() throws Exception {
      try {
         volume = connection.createVolumeInLocation(location.getId(), TAG, "EXT3", "SMALL");
         // wait up to 5 minutes for this to become "unmounted"
         assert new RetryablePredicate<Volume>(new VolumeUnmounted(connection), 300, 5,
                  TimeUnit.SECONDS).apply(volume);
      } catch (IllegalStateException e) {
         if (HttpResponseException.class.cast(e.getCause()).getResponse().getStatusCode() == 409) {
            Set<? extends Volume> volumes = connection.listVolumes();
            try {
               volume = Iterables.find(volumes, new Predicate<Volume>() {

                  @Override
                  public boolean apply(Volume input) {
                     return input.getState() == Volume.State.UNMOUNTED;
                  }

               });
            } catch (NoSuchElementException ex) {
               throw new RuntimeException("no unmounted volumes in: " + volumes, e);
            }
         } else {
            throw e;
         }
      }
      assertEquals(volume.getInstanceId(), null);
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

   @Test(dependsOnMethods = "testAddPublicKey")
   public void testCreateInstance() throws Exception {
      instance = connection.createInstanceInLocation(location.getId(), TAG, IMAGE_ID, SIZE,
               configurationData(
                        ImmutableMap.of("insight_admin_password", "myPassword1",
                                 "db2_admin_password", "myPassword2", "report_user_password",
                                 "myPassword3")).authorizePublicKey(key.getName()));
      try {
         assertIpHostAndStatusNEW(instance);
         assertConsistent(instance, TAG);
      } catch (NullPointerException e) {
         System.err.println(instance);
         throw e;
      } catch (AssertionError e) {
         System.err.println(instance);
         throw e;
      }
      
      long start = System.currentTimeMillis();
      assert new RetryablePredicate<Instance>(new InstanceActive(connection), 600, 2,
               TimeUnit.SECONDS).apply(instance) : connection.getInstance(instance.getId());

      System.out.println(((System.currentTimeMillis() - start) / 1000) + " seconds");

      try {
         assertIpHostAndStatusACTIVE(instance);
         assertConsistent(instance, TAG);
      } catch (NullPointerException e) {
         System.err.println(instance);
         throw e;
      } catch (AssertionError e) {
         System.err.println(instance);
         throw e;
      }

   }

   private void assertConsistent(Instance instance, String TAG) {
      assertNotNull(instance.getId());
      assertEquals(instance.getName(), TAG);
      assertEquals(instance.getInstanceType(), SIZE);
      assertEquals(instance.getLocation(), location.getId());
      assertEquals(instance.getImageId(), IMAGE_ID);
      assertEquals(instance.getSoftware(), SOFTWARE);
      assertEquals(instance.getKeyName(), key.getName());
      assertNotNull(instance.getLaunchTime());
      assertNotNull(instance.getExpirationTime());
      assertEquals(instance.getOwner(), user);
      assertEquals(instance.getProductCodes(), ImmutableSet.<String> of());
      assertEquals(instance.getRequestName(), TAG);
      assertNotNull(instance.getRequestId());
   }

   private void assertIpHostAndStatusNEW(Instance instance) {
      assertEquals(instance.getIp(), null);
      assertEquals(instance.getHostname(), null);
      assertEquals(instance.getStatus(), Instance.Status.NEW);
   }

   private void assertIpHostAndStatusACTIVE(Instance instance) {
      assertNotNull(instance.getIp());
      assertNotNull(instance.getHostname());
      assertEquals(instance.getStatus(), Instance.Status.ACTIVE);
   }

   /**
    * cannot run an instance due to 500 errors:
    * 
    * http://www-180.ibm.com/cloud/enterprise/beta/ram/community/discussionTopic.faces?guid={
    * DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&fid=1068&tid=1523#topic
    */
   @Test(enabled = false, dependsOnMethods = { "testAddPublicKey", "testAllocateIpAddress",
            "testCreateVolume" })
   public void testCreateInstanceWithVolume() throws Exception {
      instance2 = connection.createInstanceInLocation(location.getId(), TAG, IMAGE_ID, SIZE,
               attachIp(ip.getId()).authorizePublicKey(key.getName()).mountVolume(volume.getId(),
                        "/mnt").configurationData(
                        ImmutableMap.of("insight_admin_password", "myPassword1",
                                 "db2_admin_password", "myPassword2", "report_user_password",
                                 "myPassword3")));
      //

      volume = connection.getVolume(volume.getId());
      assertEquals(volume.getInstanceId(), instance2.getId());

      refreshIpAndReturnAllAddresses();
      assertEquals(ip.getInstanceId(), instance2.getId());
      assertEquals(ip.getIp(), instance2.getIp());

   }

   private Set<? extends Address> refreshIpAndReturnAllAddresses() {
      Set<? extends Address> allAddresses = connection.listAddresses();
      final String id = ip.getId();
      // refresh address as it may have been just created
      ip = Iterables.find(allAddresses, new Predicate<Address>() {

         @Override
         public boolean apply(Address input) {
            return input.getId().equals(id);
         }

      });
      return allAddresses;
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
      if (instance2 != null)
         try {
            connection.deleteInstance(instance2.getId());
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
