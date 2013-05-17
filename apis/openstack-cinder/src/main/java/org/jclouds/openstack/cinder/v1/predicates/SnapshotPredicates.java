/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.cinder.v1.predicates;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import org.jclouds.openstack.cinder.v1.domain.Snapshot;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.domain.Volume.Status;
import org.jclouds.openstack.cinder.v1.features.SnapshotApi;

import com.google.common.base.Predicate;

/**
 * Tests to see if snapshot has reached status. This class is most useful when paired with a RetryablePredicate as
 * in the code below. This class can be used to block execution until the Snapshot status has reached a desired state.
 * This is useful when your Snapshot needs to be 100% ready before you can continue with execution.
 *
 * <pre>
 * {@code
 * Snapshot snapshot = snapshotApi.create(volumeId);
 * RetryablePredicate<String> awaitAvailable = RetryablePredicate.create(
 *    SnapshotPredicates.available(snapshotApi), 600, 10, 10, TimeUnit.SECONDS);
 * 
 * if (!awaitAvailable.apply(snapshot.getId())) {
 *    throw new TimeoutException("Timeout on snapshot: " + snapshot); 
 * }    
 * }
 * </pre>
 * 
 * You can also use the static convenience methods as so.
 * 
 * <pre>
 * {@code
 * Snapshot snapshot = snapshotApi.create(volumeId);
 * 
 * if (!SnapshotPredicates.awaitAvailable(snapshotApi).apply(snapshot.getId())) {
 *    throw new TimeoutException("Timeout on snapshot: " + snapshot);     
 * }
 * }
 * </pre>
 * 
 * @author Everett Toews
 */
public class SnapshotPredicates {
   /**
    * Wait until a Snapshot is Available.
    * 
    * @param snapshotApi The SnapshotApi in the zone where your Snapshot resides.
    * @return RetryablePredicate That will check the status every 5 seconds for a maxiumum of 20 minutes.
    */
   public static Predicate<Snapshot> awaitAvailable(SnapshotApi snapshotApi) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(snapshotApi, Volume.Status.AVAILABLE);
      return retry(statusPredicate, 1200, 5, 5, SECONDS);
   }
   
   /**
    * Wait until a Snapshot no longer exists.
    * 
    * @param snapshotApi The SnapshotApi in the zone where your Snapshot resides.
    * @return RetryablePredicate That will check the whether the Snapshot exists 
    * every 5 seconds for a maxiumum of 20 minutes.
    */
   public static Predicate<Snapshot> awaitDeleted(SnapshotApi snapshotApi) {
      DeletedPredicate deletedPredicate = new DeletedPredicate(snapshotApi);
      return retry(deletedPredicate, 1200, 5, 5, SECONDS);
   }

   public static Predicate<Snapshot> awaitStatus(SnapshotApi snapshotApi, Volume.Status status, long maxWaitInSec,
         long periodInSec) {
   StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(snapshotApi, status);
      return retry(statusPredicate, maxWaitInSec, periodInSec, periodInSec, SECONDS);
   }
   
   private static class StatusUpdatedPredicate implements Predicate<Snapshot> {
      private SnapshotApi snapshotApi;
      private Status status;

      public StatusUpdatedPredicate(SnapshotApi snapshotApi, Volume.Status status) {
         this.snapshotApi = checkNotNull(snapshotApi, "snapshotApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the snapshot reaches status, false otherwise
       */
      @Override
      public boolean apply(Snapshot snapshot) {
         checkNotNull(snapshot, "snapshot must be defined");

         if (status.equals(snapshot.getStatus())) {
            return true;
         }
         else {
            Snapshot snapshotUpdated = snapshotApi.get(snapshot.getId());
            checkNotNull(snapshotUpdated, "Snapshot %s not found.", snapshot.getId());
            
            return status.equals(snapshotUpdated.getStatus());
         }
      }
   }

   private static class DeletedPredicate implements Predicate<Snapshot> {
      private SnapshotApi snapshotApi;

      public DeletedPredicate(SnapshotApi snapshotApi) {
         this.snapshotApi = checkNotNull(snapshotApi, "snapshotApi must be defined");
      }

      /**
       * @return boolean Return true when the snapshot is deleted, false otherwise
       */
      @Override
      public boolean apply(Snapshot snapshot) {
         checkNotNull(snapshot, "snapshot must be defined");

         return snapshotApi.get(snapshot.getId()) == null;
      }
   }
}
