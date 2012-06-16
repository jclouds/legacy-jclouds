package org.jclouds.ec2.services;

import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.jclouds.ec2.options.RegisterImageBackedByEbsOptions.Builder.addNewBlockDevice;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilderSpec;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.services.AMIClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code AMIClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class AMIClientLiveTest extends BaseComputeServiceContextLiveTest {
   private TemplateBuilderSpec ebsTemplate;

   public AMIClientLiveTest() {
      provider = "ec2";
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      String ebsSpec = setIfTestSystemPropertyPresent(overrides, provider + ".ebs-template");
      if (ebsSpec != null)
         ebsTemplate = TemplateBuilderSpec.parse(ebsSpec);
      return overrides;
   }

   protected AMIClient client;

   protected Set<String> imagesToDeregister = Sets.newHashSet();
   protected Set<String> snapshotsToDelete = Sets.newHashSet();
   protected String regionId;
   protected String ebsBackedImageId;
   protected String ebsBackedImageName = "jcloudstest1";

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getAMIServices();
      if (ebsTemplate != null) {
         Template template = view.getComputeService().templateBuilder().from(ebsTemplate).build();
         regionId = template.getLocation().getId();
         for (Image image : client.describeImagesInRegion(regionId)) {
            if (ebsBackedImageName.equals(image.getName()))
               client.deregisterImageInRegion(regionId, image.getId());
         }
      }
   }

   public void testDescribeImageNotExists() {
      assertEquals(client.describeImagesInRegion(null, imageIds("ami-cdf819a3")).size(), 0);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testDescribeImageBadId() {
      client.describeImagesInRegion(null, imageIds("asdaasdsa"));
   }

   public void testDescribeImages() {
      for (String region : view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getAvailabilityZoneAndRegionServices()
            .describeRegions().keySet()) {
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

   @Test
   public void testCreateAndListEBSBackedImage() throws Exception {
      ComputeService computeService = view.getComputeService();
      Snapshot snapshot = createSnapshot(computeService);

      // List of images before...
      int sizeBefore = computeService.listImages().size();

      // Register a new image...
      ebsBackedImageId = client.registerUnixImageBackedByEbsInRegion(regionId, ebsBackedImageName, snapshot.getId(),
            addNewBlockDevice("/dev/sda2", "myvirtual", 1).withDescription("adrian"));
      imagesToDeregister.add(ebsBackedImageId);
      final Image ebsBackedImage = Iterables.getOnlyElement(client.describeImagesInRegion(regionId,
            imageIds(ebsBackedImageId)));
      assertEquals(ebsBackedImage.getName(), ebsBackedImageName);
      assertEquals(ebsBackedImage.getImageType(), ImageType.MACHINE);
      assertEquals(ebsBackedImage.getRootDeviceType(), RootDeviceType.EBS);
      assertEquals(ebsBackedImage.getRootDeviceName(), "/dev/sda1");
      assertEquals(ebsBackedImage.getDescription(), "adrian");
      assertEquals(
            ebsBackedImage.getEbsBlockDevices().entrySet(),
            ImmutableMap.of("/dev/sda1", new Image.EbsBlockDevice(snapshot.getId(), snapshot.getVolumeSize(), true),
                  "/dev/sda2", new Image.EbsBlockDevice(null, 1, false)).entrySet());

      // This is the suggested method to ensure the new image ID is inserted
      // into the cache
      // (suggested by adriancole_ on #jclouds)
      computeService.templateBuilder().imageId(ebsBackedImage.getRegion() + "/" + ebsBackedImageId).build();

      // List of images after - should be one larger than before
      Set<? extends org.jclouds.compute.domain.Image> after = computeService.listImages();
      assertEquals(after.size(), sizeBefore + 1);

      // Detailed check: filter for the AMI ID
      Iterable<? extends org.jclouds.compute.domain.Image> filtered = Iterables.filter(after,
            ImagePredicates.idEquals(ebsBackedImage.getRegion() + "/" + ebsBackedImageId));
      assertEquals(Iterables.size(filtered), 1);
   }

   // Fires up an instance, finds its root volume ID, takes a snapshot, then
   // terminates the instance.
   private Snapshot createSnapshot(ComputeService computeService) throws RunNodesException {
      Template template = computeService.templateBuilder().from(ebsTemplate).build();
      regionId = template.getLocation().getId();
      Set<? extends NodeMetadata> nodes = computeService.createNodesInGroup("jcloudstest", 1, template);
      try {
         String instanceId = Iterables.getOnlyElement(nodes).getProviderId();
         Reservation<? extends RunningInstance> reservation = Iterables.getOnlyElement(view
               .unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getInstanceServices()
               .describeInstancesInRegion(regionId, instanceId));
         RunningInstance instance = Iterables.getOnlyElement(reservation);
         BlockDevice device = instance.getEbsBlockDevices().get("/dev/sda1");
         Snapshot snapshot = view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getElasticBlockStoreServices()
               .createSnapshotInRegion(regionId, device.getVolumeId());
         snapshotsToDelete.add(snapshot.getId());
         return snapshot;
      } finally {
         computeService.destroyNodesMatching(Predicates.in(nodes));
      }
   }

   @Test(dependsOnMethods = "testCreateAndListEBSBackedImage")
   public void testGetLaunchPermissionForImage() {
      System.out.println(client.getLaunchPermissionForImageInRegion(regionId, ebsBackedImageId));
   }

   @Override
   @AfterClass(groups = { "integration", "live" })
   protected void tearDownContext() {
      for (String imageId : imagesToDeregister)
         client.deregisterImageInRegion(regionId, imageId);
      for (String snapshotId : snapshotsToDelete)
         view.unwrap(EC2ApiMetadata.CONTEXT_TOKEN).getApi().getElasticBlockStoreServices()
               .deleteSnapshotInRegion(regionId, snapshotId);
      super.tearDownContext();
   }

}
