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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Ordering;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-ReservationInfoType.html"
 *      />
 * @author Adrian Cole
 */
public class Reservation<T extends RunningInstance> extends ForwardingSet<T> implements Comparable<Reservation<T>>{

   public static <T extends RunningInstance> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return Reservation.<T> builder().fromReservation(this);
   }

   public static class Builder<T extends RunningInstance> {
      private String region;
      private String ownerId;
      private String requesterId;
      private String reservationId;

      private ImmutableSet.Builder<T> instances = ImmutableSet.<T> builder();
      private ImmutableSet.Builder<String> groupNames = ImmutableSet.<String> builder();

      /**
       * @see Reservation#getRegion()
       */
      public Builder<T> region(String region) {
         this.region = region;
         return this;
      }

      /**
       * @see Reservation#getOwnerId()
       */
      public Builder<T> ownerId(String ownerId) {
         this.ownerId = ownerId;
         return this;
      }

      /**
       * @see Reservation#getRequesterId()
       */
      public Builder<T> requesterId(String requesterId) {
         this.requesterId = requesterId;
         return this;
      }

      /**
       * @see Reservation#getReservationId()
       */
      public Builder<T> reservationId(String reservationId) {
         this.reservationId = reservationId;
         return this;
      }

      /**
       * @see Reservation#iterator
       */
      public Builder<T> instance(T instance) {
         this.instances.add(checkNotNull(instance, "instance"));
         return this;
      }

      /**
       * @see Reservation#iterator
       */
      public Builder<T> instances(Set<T> instances) {
         this.instances.addAll(checkNotNull(instances, "instances"));
         return this;
      }

      /**
       * @see Reservation#getGroupNames()
       */
      public Builder<T> groupName(String groupName) {
         this.groupNames.add(checkNotNull(groupName, "groupName"));
         return this;
      }

      /**
       * @see Reservation#getGroupNames()
       */
      public Builder<T> groupNames(Iterable<String> groupNames) {
         this.groupNames = ImmutableSet.<String> builder().addAll(checkNotNull(groupNames, "groupNames"));
         return this;
      }

      public Reservation<T> build() {
         return new Reservation<T>(region, groupNames.build(), instances.build(), ownerId, requesterId, reservationId);
      }

      public Builder<T> fromReservation(Reservation<T> in) {
         return region(in.region).ownerId(in.ownerId).requesterId(in.requesterId).reservationId(in.reservationId)
                  .instances(in).groupNames(in.groupNames);
      }
   }

   private final String region;
   private final ImmutableSet<String> groupNames;
   private final ImmutableSet<T> instances;
   @Nullable
   private final String ownerId;
   @Nullable
   private final String requesterId;
   @Nullable
   private final String reservationId;

   public Reservation(String region, Iterable<String> groupNames, Iterable<T> instances, @Nullable String ownerId,
            @Nullable String requesterId, @Nullable String reservationId) {
      this.region = checkNotNull(region, "region");
      this.groupNames = ImmutableSet.copyOf(checkNotNull(groupNames, "groupNames"));
      this.instances = ImmutableSet.copyOf(checkNotNull(instances, "instances"));
      this.ownerId = ownerId;
      this.requesterId = requesterId;
      this.reservationId = reservationId;
   }

   @Override
   protected Set<T> delegate() {
      return instances;
   }

   /**
    * To be removed in jclouds 1.6 <h4>Warning</h4>
    * 
    * Especially on EC2 clones that may not support regions, this value is fragile. Consider
    * alternate means to determine context.
    */
   @Deprecated
   public String getRegion() {
      return region;
   }
   
   /**
    * Names of the security groups.
    */
   public Set<String> getGroupNames() {
      return groupNames;
   }

   /**
    * AWS Access Key ID of the user who owns the reservation.
    */
   public String getOwnerId() {
      return ownerId;
   }

   /**
    * The ID of the requester that launched the instances on your behalf (for example, AWS
    * Management Console or Auto Scaling).
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
      return Objects.hashCode(region, reservationId, super.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      @SuppressWarnings("unchecked")
      Reservation<T> that = Reservation.class.cast(obj);
      return super.equals(that) && Objects.equal(this.region, that.region)
               && Objects.equal(this.reservationId, that.reservationId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("region", region).add("reservationId", reservationId)
               .add("requesterId", requesterId).add("instances", instances).add("groupNames", groupNames).toString();
   }

   @Override
   public int compareTo(Reservation<T> other) {
      return ComparisonChain.start().compare(region, other.region)
               .compare(reservationId, other.reservationId, Ordering.natural().nullsLast()).result();
   }

}
