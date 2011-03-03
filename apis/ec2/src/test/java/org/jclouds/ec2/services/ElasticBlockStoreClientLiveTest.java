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

package org.jclouds.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.ec2.options.DescribeSnapshotsOptions.Builder.snapshotIds;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.aws.domain.Region;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.predicates.SnapshotCompleted;
import org.jclouds.ec2.predicates.VolumeAvailable;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.RestContext;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ElasticBlockStoreClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public class ElasticBlockStoreClientLiveTest {
   private ElasticBlockStoreClient client;
   private RestContext<EC2Client, EC2AsyncClient> context;
   private String volumeId;
   private Snapshot snapshot;
   protected String provider = "ec2";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   @BeforeClass
   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = System.getProperty("test." + provider + ".credential");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      if (credential != null)
         overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();
      client = context.getApi().getElasticBlockStoreServices();
   }

   @Test
   void testDescribeVolumes() {
      for (String region : context.getApi().getAvailabilityZoneAndRegionServices().describeRegions().keySet()) {
         SortedSet<Volume> allResults = Sets.newTreeSet(client.describeVolumesInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Volume volume = allResults.last();
            SortedSet<Volume> result = Sets.newTreeSet(client.describeVolumesInRegion(region, volume.getId()));
            assertNotNull(result);
            Volume compare = result.last();
            assertEquals(compare, volume);
         }
      }
   }

   @Test
   void testCreateVolumeInAvailabilityZone() {
      Volume expected = client.createVolumeInAvailabilityZone(getDefaultAvailabilityZone(), 1);
      assertNotNull(expected);
      System.out.println(expected);
      assertEquals(expected.getAvailabilityZone(), getDefaultAvailabilityZone());

      this.volumeId = expected.getId();

      Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(null, expected.getId()));
      assertNotNull(result);
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getId(), expected.getId());
   }

   @Test(dependsOnMethods = "testCreateVolumeInAvailabilityZone")
   void testCreateSnapshotInRegion() {
      Snapshot snapshot = client.createSnapshotInRegion(null, volumeId);
      Predicate<Snapshot> snapshotted = new RetryablePredicate<Snapshot>(new SnapshotCompleted(client), 600, 10,
            TimeUnit.SECONDS);
      assert snapshotted.apply(snapshot);

      Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(snapshot.getRegion(),
            snapshotIds(snapshot.getId())));

      assertEquals(result.getProgress(), 100);
      this.snapshot = result;
   }

   protected  String getDefaultAvailabilityZone(){
      return "us-east-1a";
   }
   
   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testCreateVolumeFromSnapshotInAvailabilityZone() {
      Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(getDefaultAvailabilityZone(), snapshot.getId());
      assertNotNull(volume);

      Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(client), 600, 10,
            TimeUnit.SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(snapshot.getRegion(), volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), getDefaultAvailabilityZone());
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(snapshot.getRegion(), volume.getId());
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testCreateVolumeFromSnapshotInAvailabilityZoneWithSize() {
      Volume volume = client.createVolumeFromSnapshotInAvailabilityZone(getDefaultAvailabilityZone(), 2,
            snapshot.getId());
      assertNotNull(volume);

      Predicate<Volume> availabile = new RetryablePredicate<Volume>(new VolumeAvailable(client), 600, 10,
            TimeUnit.SECONDS);
      assert availabile.apply(volume);

      Volume result = Iterables.getOnlyElement(client.describeVolumesInRegion(snapshot.getRegion(), volume.getId()));
      assertEquals(volume.getId(), result.getId());
      assertEquals(volume.getSnapshotId(), snapshot.getId());
      assertEquals(volume.getAvailabilityZone(), getDefaultAvailabilityZone());
      assertEquals(volume.getSize(), 2);
      assertEquals(result.getStatus(), Volume.Status.AVAILABLE);

      client.deleteVolumeInRegion(snapshot.getRegion(), volume.getId());
   }

   @Test
   void testAttachVolumeInRegion() {
      // TODO: need an instance
   }

   @Test
   void testDetachVolumeInRegion() {
      // TODO: need an instance
   }

   @Test
   void testDescribeSnapshots() {
      for (String region : Lists.newArrayList(null, Region.EU_WEST_1, Region.US_EAST_1, Region.US_WEST_1,
            Region.AP_SOUTHEAST_1)) {
         SortedSet<Snapshot> allResults = Sets.newTreeSet(client.describeSnapshotsInRegion(region));
         assertNotNull(allResults);
         if (allResults.size() >= 1) {
            Snapshot snapshot = allResults.last();
            Snapshot result = Iterables.getOnlyElement(client.describeSnapshotsInRegion(region,
                  snapshotIds(snapshot.getId())));
            assertNotNull(result);
            assertEquals(result, snapshot);
         }
      }
   }

   @Test(enabled = false)
   public void testAddCreateVolumePermissionsToSnapshot() {
      // TODO client.addCreateVolumePermissionsToSnapshotInRegion(null, userIds,
      // userGroups,
      // snapshotId);
   }

   @Test(enabled = false)
   public void testRemoveCreateVolumePermissionsFromSnapshot() {
      // TODO client.removeCreateVolumePermissionsFromSnapshotInRegion(null, userIds,
      // userGroups,
      // snapshotId);
   }

   @Test(enabled = false)
   public void testResetCreateVolumePermissionsOnSnapshot() {
      // TODO client.resetCreateVolumePermissionsOnSnapshotInRegion(null, snapshotId);
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   public void testGetCreateVolumePermissionForSnapshot() {
      System.out.println(client.getCreateVolumePermissionForSnapshotInRegion(snapshot.getRegion(), snapshot.getId()));
   }

   @Test(dependsOnMethods = "testCreateSnapshotInRegion")
   void testDeleteVolumeInRegion() {
      client.deleteVolumeInRegion(null, volumeId);
      Set<Volume> result = Sets.newLinkedHashSet(client.describeVolumesInRegion(null, volumeId));
      assertEquals(result.size(), 1);
      Volume volume = result.iterator().next();
      assertEquals(volume.getStatus(), Volume.Status.DELETING);
   }

   @Test(dependsOnMethods = "testGetCreateVolumePermissionForSnapshot")
   void testDeleteSnapshotInRegion() {
      client.deleteSnapshotInRegion(snapshot.getRegion(), snapshot.getId());
      assert client.describeSnapshotsInRegion(snapshot.getRegion(), snapshotIds(snapshot.getId())).size() == 0;
   }

   @AfterTest
   public void shutdown() {
      context.close();
   }
}
