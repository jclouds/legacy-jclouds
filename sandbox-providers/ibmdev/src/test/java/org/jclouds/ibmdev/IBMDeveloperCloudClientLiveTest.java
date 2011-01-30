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

package org.jclouds.ibmdev;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.ibmdev.options.CreateInstanceOptions.Builder.attachIp;
import static org.jclouds.ibmdev.options.CreateInstanceOptions.Builder.authorizePublicKey;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.ibmdev.domain.Address;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.domain.InstanceType;
import org.jclouds.ibmdev.domain.Key;
import org.jclouds.ibmdev.domain.Location;
import org.jclouds.ibmdev.domain.Offering;
import org.jclouds.ibmdev.domain.StorageOffering;
import org.jclouds.ibmdev.domain.Volume;
import org.jclouds.ibmdev.domain.Instance.Software;
import org.jclouds.ibmdev.domain.StorageOffering.Format;
import org.jclouds.ibmdev.predicates.AddressFree;
import org.jclouds.ibmdev.predicates.InstanceActive;
import org.jclouds.ibmdev.predicates.InstanceActiveOrFailed;
import org.jclouds.ibmdev.predicates.InstanceRemovedOrNotFound;
import org.jclouds.ibmdev.predicates.VolumeUnmounted;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.JschSshClient;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.inject.Module;

/**
 * Tests behavior of {@code IBMDeveloperCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ibmdevelopercloud.IBMDeveloperCloudClientLiveTest")
public class IBMDeveloperCloudClientLiveTest {
   private static final String OS = "Red Hat Enterprise Linux";
   private static final String VERSION = "5.4";

   private static final String PLATFORM = OS + "/" + VERSION;

   private static final ImmutableSet<Software> SOFTWARE = ImmutableSet.<Software> of(new Software(OS, "OS", VERSION));

   private static String FORMAT = "EXT3";

   private IBMDeveloperCloudClient connection;
   private Location location;
   private Address ip;
   private ImmutableMap<String, String> keyPair;
   private Key key;
   private Volume volume;
   private String identity;
   private Instance instance2;
   private Instance instance;
   private RestContext<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient> context;
   private InstanceType instanceType;
   private Image image;

   private StorageOffering cheapestStorage;

   private static final String TAG = System.getProperty("user.name");

   @BeforeGroups(groups = { "live" })
   public void setupClient() {

      String endpoint = System.getProperty("jclouds.test.endpoint");
      identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      Properties props = new Properties();
      if (endpoint != null)
         props.setProperty("ibmdev.endpoint", endpoint);
      context = new RestContextFactory().createContext("ibmdev", identity, credential, ImmutableSet
            .<Module> of(new Log4JLoggingModule()), props);

      connection = context.getApi();
      for (Instance instance : connection.listInstances()) {
         try {
            connection.deleteInstance(instance.getId());
         } catch (Exception e) {

         }
      }
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
   public void testListInstancesFromRequestReturnsEmptySet() throws Exception {
      Set<? extends Instance> response = connection.listInstancesFromRequest(Long.MAX_VALUE + "");
      assertEquals(response.size(), 0);
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
            if (instance.getStatus() == Instance.Status.FAILED || instance.getStatus() == Instance.Status.ACTIVE) {
               killInstance(instance.getId());
            }
         }
      }
      assertEquals(key.getName(), TAG);
      assert keyPair.get("public").indexOf(key.getKeyMaterial()) > 0;
      assertNotNull(key.getLastModifiedTime());
   }

   @Test(dependsOnMethods = "resolveImageAndInstanceType")
   public void testAllocateIpAddress() throws Exception {

      Offering offering = Iterables.find(connection.listAddressOfferings(), new Predicate<Offering>() {

         @Override
         public boolean apply(Offering arg0) {
            return arg0.getLocation().equals(location.getId());
         }
      });

      try {
         ip = connection.allocateAddressInLocation(location.getId(), offering.getId());
         System.out.println(ip);
         assertEquals(ip.getIp(), null);
         // wait up to 30 seconds for this to become "free"
         new RetryablePredicate<Address>(new AddressFree(connection), 30, 2, TimeUnit.SECONDS).apply(ip);
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

   @Test(dependsOnMethods = "testGetLocation")
   public void testResolveVolumeOffering() throws Exception {

      Ordering<StorageOffering> cheapestOrdering = new Ordering<StorageOffering>() {
         public int compare(StorageOffering left, StorageOffering right) {
            return ComparisonChain.start().compare(left.getPrice().getRate(), right.getPrice().getRate()).result();
         }
      }.reverse();

      Iterable<? extends StorageOffering> storageOfferingsThatAreInOurLocationAndCorrectFormat = filter(connection
            .listStorageOfferings(), new Predicate<StorageOffering>() {
         @Override
         public boolean apply(StorageOffering arg0) {

            return arg0.getLocation().equals(location.getId())
                  && Iterables.any(arg0.getFormats(), new Predicate<StorageOffering.Format>() {

                     @Override
                     public boolean apply(Format arg0) {
                        return arg0.getId().equals(FORMAT);
                     }

                  });
         }
      });
      cheapestStorage = cheapestOrdering.max(storageOfferingsThatAreInOurLocationAndCorrectFormat);
      System.out.println(cheapestStorage);
   }

   @Test(dependsOnMethods = "testResolveVolumeOffering")
   public void testCreateVolume() throws Exception {
      try {
         volume = connection.createVolumeInLocation(location.getId(), TAG, FORMAT, cheapestStorage.getName(),
               cheapestStorage.getId());
         // wait up to 5 minutes for this to become "unmounted"
         assert new RetryablePredicate<Volume>(new VolumeUnmounted(connection), 300, 5, TimeUnit.SECONDS).apply(volume);
      } catch (IllegalStateException e) {
         int code = HttpResponseException.class.cast(e.getCause()).getResponse().getStatusCode();
         if (code == 409 || code == 500) {
            Set<? extends Volume> volumes = connection.listVolumes();
            try {
               volume = Iterables.find(volumes, new Predicate<Volume>() {

                  @Override
                  public boolean apply(Volume input) {
                     return input.getState() == Volume.State.UNMOUNTED;
                  }

               });
            } catch (NoSuchElementException ex) {
               killInstance(TAG + 1);
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

   @Test(dependsOnMethods = "testGetLocation")
   public void resolveImageAndInstanceType() throws Exception {
      Iterable<? extends Image> imagesThatAreInOurLocationAndNotBYOL = filter(connection.listImages(),
            new Predicate<Image>() {
               @Override
               public boolean apply(Image arg0) {
                  return arg0.getLocation().equals(location.getId()) && arg0.getPlatform().equals(PLATFORM)
                        && !arg0.getName().contains("BYOL") && !arg0.getName().contains("PAYG");
               }
            });

      Ordering<InstanceType> cheapestOrdering = new Ordering<InstanceType>() {
         public int compare(InstanceType left, InstanceType right) {
            return ComparisonChain.start().compare(left.getPrice().getRate(), right.getPrice().getRate()).result();
         }
      }.reverse();

      Set<InstanceType> instanceTypes = Sets.newLinkedHashSet();

      for (Image image : imagesThatAreInOurLocationAndNotBYOL)
         Iterables.addAll(instanceTypes, image.getSupportedInstanceTypes());

      instanceType = cheapestOrdering.max(instanceTypes);

      final InstanceType cheapestInstanceType = instanceType;
      System.err.println(cheapestInstanceType);

      image = Iterables.find(imagesThatAreInOurLocationAndNotBYOL, new Predicate<Image>() {

         @Override
         public boolean apply(Image arg0) {
            return arg0.getSupportedInstanceTypes().contains(cheapestInstanceType);
         }

      });
      System.err.println(image);
      connection.getManifest(connection.getImage(image.getId()).getManifest());
      connection.getManifest(connection.getImage(image.getId()).getDocumentation());
   }

   @Test(dependsOnMethods = { "testAddPublicKey", "resolveImageAndInstanceType" })
   public void testCreateInstance() throws Exception {
      killInstance(TAG);

      instance = connection.createInstanceInLocation(location.getId(), TAG, image.getId(), instanceType.getId(),
            authorizePublicKey(key.getName()));

      assertBeginState(instance, TAG);
      assertIpHostNullAndStatusNEW(instance);
      blockUntilRunning(instance);
      instance = assertRunning(instance, TAG);
      sshAndDf(new IPSocket(instance.getIp(), 22), new Credentials("idcuser", key.getKeyMaterial()));
   }

   private void killInstance(final String nameToKill) {
      Set<? extends Instance> instances = connection.listInstances();
      try {
         Instance instance = Iterables.find(instances, new Predicate<Instance>() {

            @Override
            public boolean apply(Instance input) {
               return input.getName().equals(nameToKill);
            }

         });
         if (instance.getStatus() != Instance.Status.DEPROVISIONING
               && instance.getStatus() != Instance.Status.DEPROVISION_PENDING) {
            System.out.println("deleting instance: " + instance);
            int timeout = (instance.getStatus() == Instance.Status.NEW || instance.getStatus() == Instance.Status.PROVISIONING) ? 300
                  : 30;
            assert new RetryablePredicate<Instance>(new InstanceActiveOrFailed(connection), timeout, 2,
                  TimeUnit.SECONDS).apply(instance) : instance;
            connection.deleteInstance(instance.getId());
         }
         assert new RetryablePredicate<Instance>(new InstanceRemovedOrNotFound(connection), 120, 2, TimeUnit.SECONDS)
               .apply(instance) : instance;
      } catch (NoSuchElementException ex) {
      }
   }

   private Instance assertRunning(Instance instance, String name) throws AssertionError {
      instance = connection.getInstance(instance.getId());

      try {
         assertIpHostAndStatusACTIVE(instance);
         assertConsistent(instance, name);
      } catch (NullPointerException e) {
         System.err.println(instance);
         throw e;
      } catch (AssertionError e) {
         System.err.println(instance);
         throw e;
      }
      System.err.println("RUNNING: " + instance);
      return instance;
   }

   private void blockUntilRunning(Instance instance) {
      long start = System.currentTimeMillis();
      assert new RetryablePredicate<Instance>(new InstanceActive(connection), 15 * 60 * 1000).apply(instance) : connection
            .getInstance(instance.getId());

      System.out.println(((System.currentTimeMillis() - start) / 1000) + " seconds");
   }

   private void assertBeginState(Instance instance, String name) throws AssertionError {
      try {
         assertConsistent(instance, name);
      } catch (NullPointerException e) {
         System.err.println(instance);
         throw e;
      } catch (AssertionError e) {
         killInstance(instance.getId());
         throw e;
      }
   }

   private void assertConsistent(Instance instance, String name) {
      assertNotNull(instance.getId());
      assertEquals(instance.getName(), name);
      assertEquals(instance.getInstanceType(), instanceType.getId());
      assertEquals(instance.getLocation(), location.getId());
      assertEquals(instance.getImageId(), image.getId());
      assertEquals(instance.getSoftware(), SOFTWARE);
      assertEquals(instance.getKeyName(), key.getName());
      assertNotNull(instance.getLaunchTime());
      assertNotNull(instance.getExpirationTime());
      assertEquals(instance.getOwner(), identity);
      assertEquals(instance.getProductCodes(), ImmutableSet.<String> of());
      assertEquals(instance.getRequestName(), name);
      assertNotNull(instance.getRequestId());
   }

   private void assertIpHostNullAndStatusNEW(Instance instance) {
      assertEquals(instance.getIp(), null);
      assertEquals(instance.getHostname(), null);
      assertEquals(instance.getStatus(), Instance.Status.NEW);
   }

   private void assertIpHostAndStatusNEW(Instance instance) {
      assertNotNull(instance.getIp());
      assertNotNull(instance.getHostname());
      assertEquals(instance.getStatus(), Instance.Status.NEW);
   }

   private void assertIpHostAndStatusACTIVE(Instance instance) {
      assertNotNull(instance.getIp());
      assertNotNull(instance.getHostname());
      assertEquals(instance.getStatus(), Instance.Status.ACTIVE);
   }

   @Test(dependsOnMethods = { "testAddPublicKey", "testAllocateIpAddress", "testCreateVolume",
         "resolveImageAndInstanceType" })
   public void testCreateInstanceWithIpAndVolume() throws Exception {
      String name = TAG + "1";
      killInstance(name);

      instance2 = connection.createInstanceInLocation(location.getId(), name, image.getId(), instanceType.getId(),
            attachIp(ip.getId()).authorizePublicKey(key.getName()).mountVolume(volume.getId(), "/mnt"));

      assertBeginState(instance2, name);
      assertIpHostAndStatusNEW(instance2);
      blockUntilRunning(instance2);
      instance2 = assertRunning(instance2, name);

      volume = connection.getVolume(volume.getId());
      assertEquals(volume.getInstanceId(), instance2.getId());

      refreshIpAndReturnAllAddresses();
      assertEquals(ip.getInstanceId(), instance2.getId());
      assertEquals(ip.getIp(), instance2.getIp());
      sshAndDf(new IPSocket(instance2.getIp(), 22), new Credentials("idcuser", keyPair.get("private")));
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

   private void sshAndDf(IPSocket socket, Credentials credentials) throws IOException {
      for (int i = 0; i < 5; i++) {// retry loop TODO replace with predicate.
         try {
            _sshAndDf(socket, credentials);
            return;
         } catch (SshException e) {
            try {
               Thread.sleep(10 * 1000);
            } catch (InterruptedException e1) {
            }
            continue;
         }
      }
   }

   private void _sshAndDf(IPSocket socket, Credentials credentials) {
      RetryablePredicate<IPSocket> socketOpen = new RetryablePredicate<IPSocket>(new InetSocketAddressConnect(), 180,
            5, TimeUnit.SECONDS);

      socketOpen.apply(socket);

      SshClient ssh = new JschSshClient(new BackoffLimitedRetryHandler(), socket, 60000, credentials.identity, null,
            credentials.credential.getBytes());
      try {
         ssh.connect();
         ExecResponse hello = ssh.exec("echo hello");
         assertEquals(hello.getOutput().trim(), "hello");
         ExecResponse exec = ssh.exec("df");
         assertTrue(exec.getOutput().contains("Filesystem"),
               "The output should've contained filesystem information, but it didn't. Output: " + exec);
      } finally {
         if (ssh != null)
            ssh.disconnect();
      }
   }

   @BeforeGroups(groups = { "live" })
   protected void setupKeyPair() throws FileNotFoundException, IOException {
      String secretKeyFile;
      try {
         secretKeyFile = checkNotNull(System.getProperty("jclouds.test.ssh.keyfile"), "jclouds.test.ssh.keyfile");
      } catch (NullPointerException e) {
         secretKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
      }
      String secret = Files.toString(new File(secretKeyFile), Charsets.UTF_8);
      assert secret.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + secret;
      keyPair = ImmutableMap.<String, String> of("private", secret, "public", Files.toString(new File(secretKeyFile
            + ".pub"), Charsets.UTF_8));
   }
}
