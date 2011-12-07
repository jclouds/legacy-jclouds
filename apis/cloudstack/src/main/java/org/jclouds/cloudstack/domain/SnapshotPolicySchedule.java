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
package org.jclouds.cloudstack.domain;

/**
 * Describes the schedule of a snapshot policy.
 *
 * @see org.jclouds.cloudstack.util.SnapshotPolicySchedules
 * @author Richard Downer
 */
public class SnapshotPolicySchedule {

   private Snapshot.Interval interval;
   private String time;

   public SnapshotPolicySchedule(Snapshot.Interval interval, String time) {
      this.interval = interval;
      this.time = time;
   }

   public Snapshot.Interval getInterval() {
      return interval;
   }

   public String getTime() {
      return time;
   }

   @Override
   public String toString() {
      return "SnapshotPolicySchedule{" +
            "interval=" + interval +
            ", time='" + time + '\'' +
            '}';
   }
}
