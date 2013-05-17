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

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Describes the schedule of a snapshot policy.
 *
 * @author Richard Downer
 * @see org.jclouds.cloudstack.util.SnapshotPolicySchedules
 */
public class SnapshotPolicySchedule {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSnapshotPolicySchedule(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected Snapshot.Interval interval;
      protected String time;

      /**
       * @see SnapshotPolicySchedule#getInterval()
       */
      public T interval(Snapshot.Interval interval) {
         this.interval = interval;
         return self();
      }

      /**
       * @see SnapshotPolicySchedule#getTime()
       */
      public T time(String time) {
         this.time = time;
         return self();
      }

      public SnapshotPolicySchedule build() {
         return new SnapshotPolicySchedule(interval, time);
      }

      public T fromSnapshotPolicySchedule(SnapshotPolicySchedule in) {
         return this
               .interval(in.getInterval())
               .time(in.getTime());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Snapshot.Interval interval;
   private final String time;

   @ConstructorProperties({
         "interval", "time"
   })
   protected SnapshotPolicySchedule(@Nullable Snapshot.Interval interval, @Nullable String time) {
      this.interval = interval;
      this.time = time;
   }

   @Nullable
   public Snapshot.Interval getInterval() {
      return this.interval;
   }

   @Nullable
   public String getTime() {
      return this.time;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(interval, time);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      SnapshotPolicySchedule that = SnapshotPolicySchedule.class.cast(obj);
      return Objects.equal(this.interval, that.interval)
            && Objects.equal(this.time, that.time);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("interval", interval).add("time", time);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
