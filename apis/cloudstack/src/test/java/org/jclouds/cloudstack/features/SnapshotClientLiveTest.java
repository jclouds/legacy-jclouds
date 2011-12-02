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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.options.ListSnapshotsOptions;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.PredicateCallable;
import org.jclouds.predicates.Retryables;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code SnapshotClient}
 *
 * @author grkvlt@apache.org, Alex Heneveld
 */
@Test(groups = "live", singleThreaded = true, testName = "SnapshotClientLiveTest")
public class SnapshotClientLiveTest extends BaseCloudStackClientLiveTest {

   @Resource Logger logger = Logger.NULL;
   
   public void testListSnapshots() {
      Set<Snapshot> snapshots = client.getSnapshotClient().listSnapshots();
      assertNotNull(snapshots);
      assertFalse(snapshots.isEmpty());

      for (Snapshot snapshot : snapshots) {
         checkSnapshot(snapshot);
      }
   }

   public void testListSnapshotsById() {
      Iterable<Long> snapshotIds = Iterables.transform(client.getSnapshotClient().listSnapshots(), new Function<Snapshot, Long>() {
          public Long apply(Snapshot input) {
              return input.getId();
          }
      });
      assertNotNull(snapshotIds);
      assertFalse(Iterables.isEmpty(snapshotIds));

      for (Long id : snapshotIds) {
         Set<Snapshot> found = client.getSnapshotClient().listSnapshots(ListSnapshotsOptions.Builder.id(id));
         assertNotNull(found);
         assertEquals(1, found.size());
         Snapshot snapshot = Iterables.getOnlyElement(found);
         assertEquals(id.longValue(), snapshot.getId());
         checkSnapshot(snapshot);
      }
   }

   public void testListSnapshotsNonexistantId() {
      Set<Snapshot> found = client.getSnapshotClient().listSnapshots(ListSnapshotsOptions.Builder.id(-1));
      assertNotNull(found);
      assertTrue(found.isEmpty());
   }

   public void testGetSnapshotById() {
      Iterable<Long> snapshotIds = Iterables.transform(client.getSnapshotClient().listSnapshots(), new Function<Snapshot, Long>() {
          public Long apply(Snapshot input) {
              return input.getId();
          }
      });
      assertNotNull(snapshotIds);
      assertFalse(Iterables.isEmpty(snapshotIds));

      for (Long id : snapshotIds) {
         Snapshot found = client.getSnapshotClient().getSnapshot(id);
         assertNotNull(found);
         assertEquals(id.longValue(), found.getId());
         checkSnapshot(found);
      }
   }

   public void testGetSnapshotNonexistantId() {
      Snapshot found = client.getSnapshotClient().getSnapshot(-1);
      assertNull(found);
   }

   protected Volume getPreferredVolume() {
      for (Volume candidate : client.getVolumeClient().listVolumes()) {
         if (candidate.getState() == Volume.State.READY)
            return candidate;
      }
      throw new AssertionError("No suitable Volume found.");
   }

   public void testCreateSnapshotFromVolume() {
      final Volume volume = getPreferredVolume();  //fail fast if none

      Snapshot snapshot = Retryables.retryGettingResultOrFailing(new PredicateCallable<Snapshot>() {
         public Snapshot call() {
            logger.info("creating snapshot from volume %s", volume);
            AsyncCreateResponse job = client.getSnapshotClient().createSnapshot(volume.getId());
            assertTrue(jobComplete.apply(job.getJobId()));
            return findSnapshotWithId(job.getId());
         }
         protected void onFailure() {
            logger.info("failed creating snapshot (retrying): %s", getLastFailure());
         }
      }, null, 60*1000, "failed to create snapshot");
      logger.info("created snapshot %s from volume %s", snapshot, volume);
      checkSnapshot(snapshot);
      client.getSnapshotClient().deleteSnapshot(snapshot.getId());
   }

   private void checkSnapshot(final Snapshot snapshot) {
      assertNotNull(snapshot.getId());
      assertNotNull(snapshot.getName());
      assertNotSame(Snapshot.Type.UNRECOGNIZED, snapshot.getSnapshotType());
   }

   private Snapshot findSnapshotWithId(final long id) {
      return find(client.getSnapshotClient().listSnapshots(), new Predicate<Snapshot>() {
         @Override
         public boolean apply(Snapshot arg0) {
            return arg0.getId() == id;
         }
      });
   }
}
