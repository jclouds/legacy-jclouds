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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-ReservationInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class Reservation<T extends RunningInstance> extends LinkedHashSet<T> implements Comparable<Reservation<T>>,
         Set<T> {

   /** The serialVersionUID */
   private static final long serialVersionUID = -9051777593518861395L;
   private final String region;
   private final Set<String> groupIds = Sets.newLinkedHashSet();
   @Nullable
   private final String ownerId;
   @Nullable
   private final String requesterId;
   @Nullable
   private final String reservationId;

   public Reservation(String region, Iterable<String> groupIds, Iterable<T> instances, @Nullable String ownerId,
            @Nullable String requesterId, @Nullable String reservationId) {
      this.region = checkNotNull(region, "region");
      Iterables.addAll(this.groupIds, checkNotNull(groupIds, "groupIds"));
      Iterables.addAll(this, checkNotNull(instances, "instances"));
      this.ownerId = ownerId;
      this.requesterId = requesterId;
      this.reservationId = reservationId;
   }

   /**
    * Instances are tied to Availability Zones. However, the instance ID is tied to the Region.
    */
   public String getRegion() {
      return region;
   }

   public int compareTo(Reservation<T> o) {
      return (this == o) ? 0 : getReservationId().compareTo(o.getReservationId());
   }

   /**
    * Names of the security groups.
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * AWS Access Key ID of the user who owns the reservation.
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * ID of the requester.
    */
   public String getRequesterId() {
      return requesterId;
   }

   /**
    * Unique ID of the reservation.
    */
   public String getReservationId() {
      return reservationId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((groupIds == null) ? 0 : groupIds.hashCode());
      result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
      result = prime * result + ((region == null) ? 0 : region.hashCode());
      result = prime * result + ((requesterId == null) ? 0 : requesterId.hashCode());
      result = prime * result + ((reservationId == null) ? 0 : reservationId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      Reservation<?> other = (Reservation<?>) obj;
      if (groupIds == null) {
         if (other.groupIds != null)
            return false;
      } else if (!groupIds.equals(other.groupIds))
         return false;
      if (ownerId == null) {
         if (other.ownerId != null)
            return false;
      } else if (!ownerId.equals(other.ownerId))
         return false;
      if (region == null) {
         if (other.region != null)
            return false;
      } else if (!region.equals(other.region))
         return false;
      if (requesterId == null) {
         if (other.requesterId != null)
            return false;
      } else if (!requesterId.equals(other.requesterId))
         return false;
      if (reservationId == null) {
         if (other.reservationId != null)
            return false;
      } else if (!reservationId.equals(other.reservationId))
         return false;
      return true;
   }

}
