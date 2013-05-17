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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class SnapshotPolicy
 *
 * @author Richard Downer
 */
public class SnapshotPolicy {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSnapshotPolicy(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected Snapshot.Interval interval;
      protected long numberToRetain;
      protected String schedule;
      protected String timezone;
      protected String volumeId;

      /**
       * @see SnapshotPolicy#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see SnapshotPolicy#getInterval()
       */
      public T interval(Snapshot.Interval interval) {
         this.interval = interval;
         return self();
      }

      /**
       * @see SnapshotPolicy#getNumberToRetain()
       */
      public T numberToRetain(long numberToRetain) {
         this.numberToRetain = numberToRetain;
         return self();
      }

      /**
       * @see SnapshotPolicy#getSchedule()
       */
      public T schedule(String schedule) {
         this.schedule = schedule;
         return self();
      }

      /**
       * @see SnapshotPolicy#getTimezone()
       */
      public T timezone(String timezone) {
         this.timezone = timezone;
         return self();
      }

      /**
       * @see SnapshotPolicy#getVolumeId()
       */
      public T volumeId(String volumeId) {
         this.volumeId = volumeId;
         return self();
      }

      public SnapshotPolicy build() {
         return new SnapshotPolicy(id, interval, numberToRetain, schedule, timezone, volumeId);
      }

      public T fromSnapshotPolicy(SnapshotPolicy in) {
         return this
               .id(in.getId())
               .interval(in.getInterval())
               .numberToRetain(in.getNumberToRetain())
               .schedule(in.getSchedule())
               .timezone(in.getTimezone())
               .volumeId(in.getVolumeId());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final Snapshot.Interval interval;
   private final long numberToRetain;
   private final String schedule;
   private final String timezone;
   private final String volumeId;

   @ConstructorProperties({
         "id", "intervaltype", "maxsnaps", "schedule", "timezone", "volumeid"
   })
   protected SnapshotPolicy(String id, @Nullable Snapshot.Interval interval, long numberToRetain, @Nullable String schedule,
                            @Nullable String timezone, @Nullable String volumeId) {
      this.id = checkNotNull(id, "id");
      this.interval = interval;
      this.numberToRetain = numberToRetain;
      this.schedule = schedule;
      this.timezone = timezone;
      this.volumeId = volumeId;
   }

   /**
    * @return the ID of the snapshot policy
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return valid types are hourly, daily, weekly, monthly, template, and none.
    */
   @Nullable
   public Snapshot.Interval getInterval() {
      return this.interval;
   }

   /**
    * @return maximum number of snapshots retained
    */
   public long getNumberToRetain() {
      return this.numberToRetain;
   }

   /**
    * @return time the snapshot is scheduled to be taken.
    */
   @Nullable
   public String getSchedule() {
      return this.schedule;
   }

   /**
    * @return the time zone of the snapshot policy
    */
   @Nullable
   public String getTimezone() {
      return this.timezone;
   }

   /**
    * @return ID of the disk volume
    */
   @Nullable
   public String getVolumeId() {
      return this.volumeId;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, interval, numberToRetain, schedule, timezone, volumeId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SnapshotPolicy that = SnapshotPolicy.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.interval, that.interval)
            && Objects.equal(this.numberToRetain, that.numberToRetain)
            && Objects.equal(this.schedule, that.schedule)
            && Objects.equal(this.timezone, that.timezone)
            && Objects.equal(this.volumeId, that.volumeId);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("interval", interval).add("numberToRetain", numberToRetain).add("schedule", schedule).add("timezone", timezone)
            .add("volumeId", volumeId);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
