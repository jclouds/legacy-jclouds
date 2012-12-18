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
package org.jclouds.cloudstack.ec2.services;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jclouds.cloudstack.ec2.CloudStackEC2ApiMetadata;
import org.jclouds.cloudstack.ec2.CloudStackEC2Client;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.predicates.InstanceStateRunning;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.jclouds.ec2.services.ElasticBlockStoreClientLiveTest;
import org.jclouds.predicates.RetryablePredicate;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudStackEC2ElasticBlockStoreClientLiveTest")
public class CloudStackEC2ElasticBlockStoreClientLiveTest extends ElasticBlockStoreClientLiveTest {
   private CloudStackEC2Client cloudstackEc2Client;
   private RetryablePredicate<RunningInstance> runningTester;
   private ElasticBlockStoreClient cloudstackClient;
   private String regionId = "AmazonEC2";
   private String cloudstackDefaultZone;
   private String imageId;
   private RunningInstance instance;
   private String instanceId;
   private String cloudstackVolumeId;
   private Snapshot cloudstackSnapshot;
   private Volume volumeSnapshot;

   public CloudStackEC2ElasticBlockStoreClientLiveTest() {
      provider = "cloudstack-ec2";
   }

   @Override
   @BeforeClass(groups = {"integration", "live"})
   public void setupContext() {
      initializeContext();
      cloudstackEc2Client = view.unwrap(CloudStackEC2ApiMetadata.CONTEXT_TOKEN).getApi();
      runningTester = new RetryablePredicate<RunningInstance>(new InstanceStateRunning(cloudstackEc2Client), 900, 5,
              TimeUnit.SECONDS);
      cloudstackClient = cloudstackEc2Client.getElasticBlockStoreServices();
      Set<AvailabilityZoneInfo> allResults = cloudstackEc2Client.getAvailabilityZoneAndRegionServices()
              .describeAvailabilityZonesInRegion(regionId);
      allResults.iterator().next();
      cloudstackDefaultZone = allResults.iterator().next().getZone();
      Set<? extends Image> allImageResults = cloudstackEc2Client.getAMIServices().describeImagesInRegion(regionId);
      assertNotNull(allImageResults);
      assert allImageResults.size() >= 1 : allImageResults.size();
      Iterator<? extends Image> iterator = allImageResults.iterator();
      imageId = iterator.next().getId();
      if (imageId != null) {
         runInstance();
      }
   }

   private void runInstance() {
      Reservation<? extends RunningInstance> runningInstances = cloudstackEc2Client.getInstanceServices()
              .runInstancesInRegion(
                      regionId, cloudstackDefaultZone, imageId, 1, 1);
      instance = getOnlyElement(concat(runningInstances));
      instanceId = instance.getId();
      assertTrue(runningTester.apply(instance), instanceId + "didn't achieve the state running!");
      instance = (RunningInstance) (getOnlyElement(concat(cloudstackEc2Client.getInstanceServices()
              .describeInstancesInRegion(regionId,
                      instanceId))));
   }

   @Test
   void testDescribeVolumes() {
      SortedSet<Volume> allResults = Sets.newTreeSet(cloudstackClient.describeVolumesInRegion(regionId));
      assertNotNull(allResults);
      if (allResults.size() >= 1) {
         Volume volume = allResults.last();
         SortedSet<Volume> result = Sets.newTreeSet(cloudstackClient.describeVolumesInRegion(regionId, volume.getId()));
         assertNotNull(result);
         Volume compare = result.last();
         assertEquals(compare, volume);
      }
   }

   @Test
   void testCreateVolumeInAvailabilityZone() {
      Volume expected = cloudstackClient.createVolumeInAvailabilityZone(cloudstackDefaultZone, 1);
      assertNotNull(expected);
      assertEquals(expected.getAvailabilityZone(), cloudstackDefaultZone);
      this.cloudstackVolumeId = expected.getId();
      Set<Volume> result = Sets.newLinkedHashSet(cloudstackClient.describeVolumesInRegion(regionId, expected.getId()));
      assertNotNull(result);
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getId(), expected.getId());
   }

   @Test(dependsOnMethods = "testAttachVolumeInRegion")
   void testCreateSnapshotInRegion() {
      Snapshot snapshot = cloudstackClient.createSnapshotInRegion(regionId, cloudstackVolumeId);
      Predicate<Snapshot> snapshotted = new RetryablePredicate<Snapshot>(new SnapshotCompleted(cloudstackClient),
              900, 10,
              TimeUnit.SECONDS);
      assert snapshotted.apply(snapshot);

      Snapshot result = Iterables.getOnlyElement(cloudstackClient.describeSnapshotsInRegion(snapshot.getRegion(),
              snapshotIds(snapshot.getId())));

      assertEquals(result.getProgress(), 100);
      this.cloudstackSnapshot = result;
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testCreateVolumeFromSnapshotInAvailabilityZone() {
      volumeSnapshot = cloudstackClient.createVolumeFromSnapshotInAvailabilityZone(cloudstackDefaultZone,
              cloudstackSnapshot.getId());
      assertNotNull(volumeSnapshot);

      Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(cloudstackClient), 600, 10,
              TimeUnit.SECONDS);
      assert availabile.apply(volumeSnapshot);

      Volume result = Iterables.getOnlyElement(cloudstackClient.describeVolumesInRegion(regionId,
              volumeSnapshot.getId()));

      assertEquals(volumeSnapshot.getId(), result.getId());
      // assertEquals(volumeSnapshot.getSnapshotId(), cloudstackSnapshot.getId());
      // assertEquals(volumeSnapshot.getAvailabilityZone(), defaultZone);
      //assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      cloudstackClient.deleteVolumeInRegion(regionId, volumeSnapshot.getId());
      volumeSnapshot = null;
   }

   @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
   void testAttachVolumeInRegion() {
      cloudstackClient.attachVolumeInRegion(regionId, cloudstackVolumeId, instanceId, "/dev/sdh");
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testDetachVolumeInRegion() {
      cloudstackClient.detachVolumeInRegion(regionId, cloudstackVolumeId, false);
   }

   @Test
   void testDescribeSnapshots() {
      for (String region : cloudstackEc2Client.getConfiguredRegions()) {
         SortedSet<Snapshot> allResults = Sets.newTreeSet(cloudstackClient.describeSnapshotsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Snapshot snapshot = allResults.last();
            Snapshot result = Iterables.getOnlyElement(cloudstackClient.describeSnapshotsInRegion(region,
                    snapshotIds(snapshot.getId())));
            assertNotNull(result);
            assertEquals(result, snapshot);
         }
      }
   }

   @Test(dependsOnMethods = "testDetachVolumeInRegion")
   void testDeleteVolumeInRegion() {
      cloudstackClient.deleteVolumeInRegion(regionId, cloudstackVolumeId);
      cloudstackVolumeId = null;
      /*Set<Volume> result = Sets.newLinkedHashSet(cloudstackClient.describeVolumesInRegion(defaultRegion,
      cloudstackVolumeId));
        assertEquals(result.size(), 1);
        Volume volume = result.iterator().next();
        assertEquals(volume.getStatus(), Volume.Status.DELETING);*/
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testGetCreateVolumePermissionForSnapshot() {
      throw new org.testng.SkipException("Not supported in CloudStack");
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
      throw new org.testng.SkipException("Not supported in CloudStack");
   }

   @Test(dependsOnMethods = "testCreateVolumeFromSnapshotInAvailabilityZone")
   void testDeleteSnapshotInRegion() {
      cloudstackClient.deleteSnapshotInRegion(regionId, cloudstackSnapshot.getId());
      assert cloudstackClient.describeSnapshotsInRegion(regionId, snapshotIds(cloudstackSnapshot.getId())).size() == 0;
      cloudstackSnapshot = null;
   }

   @Override
   @AfterClass(groups = {"integration", "live"})
   protected void tearDownContext() {
      if (instanceId != null) {
         cloudstackEc2Client.getInstanceServices().terminateInstancesInRegion(regionId, instanceId);
      }

      if (cloudstackVolumeId != null) {
         cloudstackClient.deleteVolumeInRegion(regionId, cloudstackVolumeId);
      }

      if (cloudstackSnapshot != null) {
         cloudstackClient.deleteSnapshotInRegion(regionId, cloudstackSnapshot.getId());
      }

      if (volumeSnapshot != null) {
         cloudstackClient.deleteVolumeInRegion(regionId, volumeSnapshot.getId());
      }

      super.tearDownContext();
   }
}