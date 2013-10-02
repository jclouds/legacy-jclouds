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

import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.domain.Volume.Status;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;

import com.google.common.base.Predicate;

/**
 * Tests to see if volume has reached status. This class is most useful when paired with a RetryablePredicate as
 * in the code below. This class can be used to block execution until the Volume status has reached a desired state.
 * This is useful when your Volume needs to be 100% ready before you can continue with execution.
 *
 * <pre>
 * {@code
 * Volume volume = volumeApi.create(100);
 * 
 * RetryablePredicate<String> awaitAvailable = RetryablePredicate.create(
 *    VolumePredicates.available(volumeApi), 600, 10, 10, TimeUnit.SECONDS);
 * 
 * if (!awaitAvailable.apply(volume.getId())) {
 *    throw new TimeoutException("Timeout on volume: " + volume); 
 * }    
 * }
 * </pre>
 * 
 * You can also use the static convenience methods as so.
 * 
 * <pre>
 * {@code
 * Volume volume = volumeApi.create(100);
 * 
 * if (!VolumePredicates.awaitAvailable(volumeApi).apply(volume.getId())) {
 *    throw new TimeoutException("Timeout on volume: " + volume);     
 * }
 * }
 * </pre>
 * 
 * @author Everett Toews
 */
public class VolumePredicates {
   /**
    * Wait until a Volume is Available.
    * 
    * @param volumeApi The VolumeApi in the zone where your Volume resides.
    * @return RetryablePredicate That will check the status every 5 seconds for a maxiumum of 10 minutes.
    */
   public static Predicate<Volume> awaitAvailable(VolumeApi volumeApi) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(volumeApi, Volume.Status.AVAILABLE);
      return retry(statusPredicate, 600, 5, 5, SECONDS);
   }
   
   /**
    * Wait until a Volume is In Use.
    * 
    * @param volumeApi The VolumeApi in the zone where your Volume resides.
    * @return RetryablePredicate That will check the status every 5 seconds for a maxiumum of 10 minutes.
    */
   public static Predicate<Volume> awaitInUse(VolumeApi volumeApi) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(volumeApi, Volume.Status.IN_USE);
      return retry(statusPredicate, 600, 5, 5, SECONDS);
   }

   /**
    * Wait until a Volume no longer exists.
    * 
    * @param volumeApi The VolumeApi in the zone where your Volume resides.
    * @return RetryablePredicate That will check the whether the Volume exists 
    * every 5 seconds for a maxiumum of 10 minutes.
    */
   public static Predicate<Volume> awaitDeleted(VolumeApi volumeApi) {
      DeletedPredicate deletedPredicate = new DeletedPredicate(volumeApi);
      return retry(deletedPredicate, 600, 5, 5, SECONDS);
   }
   
   public static Predicate<Volume> awaitStatus(
         VolumeApi volumeApi, Volume.Status status, long maxWaitInSec, long periodInSec) {
      StatusUpdatedPredicate statusPredicate = new StatusUpdatedPredicate(volumeApi, status);
      return retry(statusPredicate, maxWaitInSec, periodInSec, periodInSec, SECONDS);
   }
   
   private static class StatusUpdatedPredicate implements Predicate<Volume> {
      private VolumeApi volumeApi;
      private Status status;

      public StatusUpdatedPredicate(VolumeApi volumeApi, Volume.Status status) {
         this.volumeApi = checkNotNull(volumeApi, "volumeApi must be defined");
         this.status = checkNotNull(status, "status must be defined");
      }

      /**
       * @return boolean Return true when the volume reaches status, false otherwise
       */
      @Override
      public boolean apply(Volume volume) {
         checkNotNull(volume, "volume must be defined");
         
         if (status.equals(volume.getStatus())) {
            return true;
         }
         else {
            Volume volumeUpdated = volumeApi.get(volume.getId());
            checkNotNull(volumeUpdated, "Volume %s not found.", volume.getId());
            
            return status.equals(volumeUpdated.getStatus());
         }
      }
   }

   private static class DeletedPredicate implements Predicate<Volume> {
      private VolumeApi volumeApi;

      public DeletedPredicate(VolumeApi volumeApi) {
         this.volumeApi = checkNotNull(volumeApi, "volumeApi must be defined");
      }

      /**
       * @return boolean Return true when the snapshot is deleted, false otherwise
       */
      @Override
      public boolean apply(Volume volume) {
         checkNotNull(volume, "volume must be defined");

         return volumeApi.get(volume.getId()) == null;
      }
   }
}
