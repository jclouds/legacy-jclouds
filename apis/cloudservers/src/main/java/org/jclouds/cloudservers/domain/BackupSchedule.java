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
package org.jclouds.cloudservers.domain;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A backup schedule can be defined to create server images at regular intervals (daily and weekly).
 * Backup schedules are configurable per server.
 * 
 * @author Adrian Cole
*/
public class BackupSchedule {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromBackupSchedule(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected DailyBackup daily;
      protected boolean enabled;
      protected WeeklyBackup weekly;
   
      /** 
       * @see BackupSchedule#getDaily()
       */
      public T daily(DailyBackup daily) {
         this.daily = daily;
         return self();
      }

      /** 
       * @see BackupSchedule#isEnabled()
       */
      public T enabled(boolean enabled) {
         this.enabled = enabled;
         return self();
      }

      /** 
       * @see BackupSchedule#getWeekly()
       */
      public T weekly(WeeklyBackup weekly) {
         this.weekly = weekly;
         return self();
      }

      public BackupSchedule build() {
         return new BackupSchedule(daily, enabled, weekly);
      }
      
      public T fromBackupSchedule(BackupSchedule in) {
         return this
                  .daily(in.getDaily())
                  .enabled(in.isEnabled())
                  .weekly(in.getWeekly());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final DailyBackup daily;
   private final boolean enabled;
   private final WeeklyBackup weekly;

   @ConstructorProperties({
      "daily", "enabled", "weekly"
   })
   protected BackupSchedule(@Nullable DailyBackup daily, boolean enabled, @Nullable WeeklyBackup weekly) {
      this.daily = daily;
      this.enabled = enabled;
      this.weekly = weekly;
   }

   @Nullable
   public DailyBackup getDaily() {
      return this.daily;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   @Nullable
   public WeeklyBackup getWeekly() {
      return this.weekly;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(daily, enabled, weekly);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BackupSchedule that = BackupSchedule.class.cast(obj);
      return Objects.equal(this.daily, that.daily)
               && Objects.equal(this.enabled, that.enabled)
               && Objects.equal(this.weekly, that.weekly);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("daily", daily).add("enabled", enabled).add("weekly", weekly);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
