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

package org.jclouds.rackspace.cloudservers.domain;

/**
 * A backup schedule can be defined to create server images at regular intervals (daily and weekly).
 * Backup schedules are configurable per server.
 * 
 * @author Adrian Cole
 */
public class BackupSchedule {
   protected DailyBackup daily = DailyBackup.DISABLED;
   protected boolean enabled;
   protected WeeklyBackup weekly = WeeklyBackup.DISABLED;

   public BackupSchedule() {
   }

   public BackupSchedule(WeeklyBackup weekly, DailyBackup daily, boolean enabled) {
      this.weekly = weekly;
      this.daily = daily;
      this.enabled = enabled;
   }

   public DailyBackup getDaily() {
      return daily;
   }

   public void setDaily(DailyBackup value) {
      this.daily = value;
   }

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean value) {
      this.enabled = value;
   }

   public WeeklyBackup getWeekly() {
      return weekly;
   }

   public void setWeekly(WeeklyBackup value) {
      this.weekly = value;
   }

   @Override
   public String toString() {
      return "BackupSchedule [daily=" + daily + ", enabled=" + enabled + ", weekly=" + weekly + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((daily == null) ? 0 : daily.hashCode());
      result = prime * result + (enabled ? 1231 : 1237);
      result = prime * result + ((weekly == null) ? 0 : weekly.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BackupSchedule other = (BackupSchedule) obj;
      if (daily == null) {
         if (other.daily != null)
            return false;
      } else if (!daily.equals(other.daily))
         return false;
      if (enabled != other.enabled)
         return false;
      if (weekly == null) {
         if (other.weekly != null)
            return false;
      } else if (!weekly.equals(other.weekly))
         return false;
      return true;
   }

}
