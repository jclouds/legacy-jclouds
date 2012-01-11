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
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.filters;
import static org.jclouds.aws.ec2.options.AWSDescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;
import static org.jclouds.ec2.options.RegisterImageOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.jclouds.aws.domain.Region;
import org.jclouds.compute.BaseVersionedServiceLiveTest;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code AMIClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class AMIClientLiveTest extends BaseVersionedServiceLiveTest {
   public AMIClientLiveTest() {
      provider = "aws-ec2";
      // TODO: parameterize this.
      imageId = "ami-cdf819a4";
   }
   
   private AMIClient client;
   private static final String DEFAULT_MANIFEST = "adrianimages/image.manifest.xml";
   private static final String DEFAULT_SNAPSHOT = "TODO";
   private RestContext<EC2Client, EC2AsyncClient> context;

   private Set<String> imagesToDeregister = Sets.newHashSet();
   private Set<String> snapshotsToDelete = Sets.newHashSet();

   private ComputeServiceContext jcloudsContext;
   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      jcloudsContext = new ComputeServiceContextFactory().createContext(provider,
         ImmutableSet.<Module>of(new Log4JLoggingModule()), overrides);
      context = jcloudsContext.getProviderSpecificContext();
      client = context.getApi().getAMIServices();
   }

   public void testDescribeImageNotExists() {
      assertEquals(client.describeImagesInRegion(null, imageIds("ami-cdf819a3")).size(), 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDescribeImageBadId() {
      client.describeImagesInRegion(null, imageIds("asdaasdsa"));
   }

   public void testDescribeImages() {
      for (String region : context.getApi().getAvailabilityZoneAndRegionServices().describeRegions().keySet()) {
         Set<? extends Image> allResults = client.describeImagesInRegion(region);
         assertNotNull(allResults);
         assert allResults.size() >= 2 : allResults.size();
         Iterator<? extends Image> iterator = allResults.iterator();
         String id1 = iterator.next().getId();
         String id2 = iterator.next().getId();
         Set<? extends Image> twoResults = client.describeImagesInRegion(region, imageIds(id1, id2));
         assertNotNull(twoResults);
         assertEquals(twoResults.size(), 2);
         iterator = twoResults.iterator();
         assertEquals(iterator.next().getId(), id1);
         assertEquals(iterator.next().getId(), id2);
      }
   }

   public void testDescribeImagesCC() {
      Set<? extends Image> ccResults = client.describeImagesInRegion(Region.US_EAST_1, filters(
               ImmutableMultimap.<String, String> builder()//
                        .put("virtualization-type", "hvm")//
                        .put("architecture", "x86_64")//
                        .putAll("owner-id", ImmutableSet.<String> of("137112412989", "099720109477"))//
                        .put("hypervisor", "xen")//
                        .put("state", "available")//
                        .put("image-type", "machine")//
                        .put("root-device-type", "ebs")//
                        .build()).ownedBy("137112412989", "099720109477"));
      assertNotNull(ccResults);
      assert (ccResults.size() >= 34) : ccResults;
   }

   @Test(enabled = false)
   public void testRegisterImageFromManifest() {
      String imageRegisteredId = client.registerImageFromManifestInRegion(null, "jcloudstest1", DEFAULT_MANIFEST);
      imagesToDeregister.add(imageRegisteredId);
      Image imageRegisteredFromManifest = Iterables.getOnlyElement(client.describeImagesInRegion(null,
               imageIds(imageRegisteredId)));
      assertEquals(imageRegisteredFromManifest.getName(), "jcloudstest1");
      assertEquals(imageRegisteredFromManifest.getImageLocation(), DEFAULT_MANIFEST);
      assertEquals(imageRegisteredFromManifest.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredFromManifest.getRootDeviceType(), RootDeviceType.INSTANCE_STORE);
      assertEquals(imageRegisteredFromManifest.getRootDeviceName(), "/dev/sda1");
   }

   @Test(enabled = false)
   public void testRegisterImageFromManifestOptions() {
      String imageRegisteredWithOptionsId = client.registerImageFromManifestInRegion(null, "jcloudstest2",
               DEFAULT_MANIFEST, withDescription("adrian"));
      imagesToDeregister.add(imageRegisteredWithOptionsId);
      Image imageRegisteredFromManifestWithOptions = Iterables.getOnlyElement(client.describeImagesInRegion(null,
               imageIds(imageRegisteredWithOptionsId)));
      assertEquals(imageRegisteredFromManifestWithOptions.getName(), "jcloudstest2");
      assertEquals(imageRegisteredFromManifestWithOptions.getImageLocation(), DEFAULT_MANIFEST);
      assertEquals(imageRegisteredFromManifestWithOptions.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredFromManifestWithOptions.getRootDeviceType(), RootDeviceType.INSTANCE_STORE);
      assertEquals(imageRegisteredFromManifestWithOptions.getRootDeviceName(), "/dev/sda1");
      assertEquals(imageRegisteredFromManifestWithOptions.getDescription(), "adrian");
   }

   @Test
   public void testNewlyRegisteredImageCanBeListed() throws Exception {
      ComputeService computeService = jcloudsContext.getComputeService();
      Snapshot snapshot = createSnapshot(computeService);

      // List of images before...
      int sizeBefore = computeService.listImages().size();

      // Register a new image...
      final String imageRegisteredId = client.registerUnixImageBackedByEbsInRegion(null, "jcloudstest1", snapshot.getId());
      imagesToDeregister.add(imageRegisteredId);
      final Image imageRegistered = Iterables.getOnlyElement(client.describeImagesInRegion(null, imageIds(imageRegisteredId)));

      // This is the suggested method to ensure the new image ID is inserted into the cache
      // (suggested by adriancole_ on #jclouds)
      computeService.templateBuilder().imageId(imageRegistered.getRegion() + "/" + imageRegisteredId).build();

      // List of images after - should be one larger than before
      Set<? extends org.jclouds.compute.domain.Image> after = computeService.listImages();
      assertEquals(after.size(), sizeBefore + 1);

      // Detailed check: filter for the AMI ID
      Iterable<? extends org.jclouds.compute.domain.Image> filtered = Iterables.filter(after,
         ImagePredicates.idEquals(imageRegistered.getRegion() + "/" + imageRegisteredId));
      assertEquals(Iterables.size(filtered), 1);
   }

   // Fires up an instance, finds its root volume ID, takes a snapshot, then terminates the instance.
   private Snapshot createSnapshot(ComputeService computeService) throws RunNodesException {
      Template options = computeService.templateBuilder().smallest().build();
      Set<? extends NodeMetadata> nodes = computeService.createNodesInGroup("jcloudstest", 1, options);
      try {
         String instanceId = Iterables.getOnlyElement(nodes).getProviderId();
         Reservation<? extends RunningInstance> reservation = Iterables.getOnlyElement(context.getApi().getInstanceServices().describeInstancesInRegion(null, instanceId));
         RunningInstance instance = Iterables.getOnlyElement(reservation);
         BlockDevice device = instance.getEbsBlockDevices().get("/dev/sda1");
         Snapshot snapshot = context.getApi().getElasticBlockStoreServices().createSnapshotInRegion(null, device.getVolumeId());
         snapshotsToDelete.add(snapshot.getId());
         return snapshot;
      } finally {
         computeService.destroyNodesMatching(Predicates.in(nodes));
      }
   }

   @Test(enabled = false)
   // awaiting EBS functionality to be added to jclouds
   public void testRegisterImageBackedByEBS() {
      String imageRegisteredId = client.registerUnixImageBackedByEbsInRegion(null, "jcloudstest1", DEFAULT_MANIFEST);
      imagesToDeregister.add(imageRegisteredId);
      Image imageRegistered = Iterables
               .getOnlyElement(client.describeImagesInRegion(null, imageIds(imageRegisteredId)));
      assertEquals(imageRegistered.getName(), "jcloudstest1");
      assertEquals(imageRegistered.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegistered.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(imageRegistered.getRootDeviceName(), "/dev/sda1");
   }

   @Test(enabled = false)
   // awaiting EBS functionality to be added to jclouds
   public void testRegisterImageBackedByEBSOptions() {
      String imageRegisteredWithOptionsId = client.registerUnixImageBackedByEbsInRegion(null, "jcloudstest2",
               DEFAULT_SNAPSHOT, addNewBlockDevice("/dev/sda2", "myvirtual", 1).withDescription("adrian"));
      imagesToDeregister.add(imageRegisteredWithOptionsId);
      Image imageRegisteredWithOptions = Iterables.getOnlyElement(client.describeImagesInRegion(null,
               imageIds(imageRegisteredWithOptionsId)));
      assertEquals(imageRegisteredWithOptions.getName(), "jcloudstest2");
      assertEquals(imageRegisteredWithOptions.getImageType(), ImageType.MACHINE);
      assertEquals(imageRegisteredWithOptions.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(imageRegisteredWithOptions.getRootDeviceName(), "/dev/sda1");
      assertEquals(imageRegisteredWithOptions.getDescription(), "adrian");
      assertEquals(imageRegisteredWithOptions.getEbsBlockDevices().entrySet(), ImmutableMap.of("/dev/sda1",
               new Image.EbsBlockDevice("/dev/sda1", 30, true), "/dev/sda2",
               new Image.EbsBlockDevice("/dev/sda2", 1, true)).entrySet());
   }

   @Test(enabled = false)
   public void testCreateImage() {
      // TODO client.createImageInRegion(null, name, instanceId, options);
   }

   @Test(enabled = false)
   public void testAddProductCodesToImage() {
      // TODO client.addProductCodesToImageInRegion(null, productCodes, imageId);
   }

   @Test(enabled = false)
   public void testAddLaunchPermissionsToImage() {
      // TODO client.addLaunchPermissionsToImageInRegion(null, userIds, userGroups,
      // imageId);
   }

   @Test(enabled = false)
   public void testRemoveLaunchPermissionsFromImage() {
      // TODO client.removeLaunchPermissionsFromImageInRegion(null, userIds, userGroups,
      // imageId);
   }

   @Test(enabled = false)
   public void testResetLaunchPermissionsOnImage() {
      // TODO client.resetLaunchPermissionsOnImageInRegion(null, imageId);
   }

   @Test(enabled = false)
   public void testGetLaunchPermissionForImage() {
      System.out.println(client.getLaunchPermissionForImageInRegion(null, imageId));
   }

   @Test(enabled = false)
   // awaiting ebs support
   public void testGetBlockDeviceMappingsForImage() {
      System.out.println(client.getBlockDeviceMappingsForImageInRegion(null, imageId));
   }

   @AfterTest
   public void cleanUp() {
      for (String imageId : imagesToDeregister)
         client.deregisterImageInRegion(null, imageId);
      for (String snapshotId : snapshotsToDelete)
         context.getApi().getElasticBlockStoreServices().deleteSnapshotInRegion(null, snapshotId);
   }
}
