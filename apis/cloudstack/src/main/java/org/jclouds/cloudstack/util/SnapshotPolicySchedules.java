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
package org.jclouds.cloudstack.util;

import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.SnapshotPolicySchedule;

/**
 * Methods to create SnapshotPolicySchedule objects in the format required by Cloudstack.
 * 
 * @author Richard Downer
 */
public class SnapshotPolicySchedules {
   public static SnapshotPolicySchedule hourly(int minute) {
      return SnapshotPolicySchedule.builder().interval(Snapshot.Interval.HOURLY).time(String.format("%02d", minute)).build();
   }

   public static SnapshotPolicySchedule daily(int hour, int minute) {
      return SnapshotPolicySchedule.builder().interval(Snapshot.Interval.DAILY).time(String.format("%02d:%02d", minute, hour)).build();
   }

   public static SnapshotPolicySchedule weekly(int day, int hour, int minute) {
      return SnapshotPolicySchedule.builder().interval(Snapshot.Interval.WEEKLY).time(String.format("%02d:%02d:%02d", minute, hour, day)).build();
   }

   public static SnapshotPolicySchedule monthly(int day, int hour, int minute) {
      return SnapshotPolicySchedule.builder().interval(Snapshot.Interval.MONTHLY).time(String.format("%02d:%02d:%02d", minute, hour, day)).build();
   }
}
