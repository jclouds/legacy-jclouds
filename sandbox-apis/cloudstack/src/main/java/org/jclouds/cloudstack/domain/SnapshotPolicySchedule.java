package org.jclouds.cloudstack.domain;

/**
 * Describes the schedule of a snapshot policy.
 *
 * @author Richard Downer
 */
public class SnapshotPolicySchedule {

   public static SnapshotPolicySchedule hourly(int minute) {
      return new SnapshotPolicySchedule(Snapshot.SnapshotIntervalType.HOURLY, "FIXME");
   }

   public static SnapshotPolicySchedule daily(int hour, int minute) {
      return new SnapshotPolicySchedule(Snapshot.SnapshotIntervalType.DAILY, "FIXME");
   }

   public static SnapshotPolicySchedule weekly(int day, int hour, int minute) {
      return new SnapshotPolicySchedule(Snapshot.SnapshotIntervalType.WEEKLY, "FIXME");
   }

   public static SnapshotPolicySchedule monthly(int day, int hour, int minute) {
      return new SnapshotPolicySchedule(Snapshot.SnapshotIntervalType.MONTHLY, String.format("%02d:%02d:%02d", minute, hour, day));
   }

   private Snapshot.SnapshotIntervalType intervalType;
   private String time;

   private SnapshotPolicySchedule(Snapshot.SnapshotIntervalType intervalType, String time) {
      this.intervalType = intervalType;
      this.time = time;
   }

   public Snapshot.SnapshotIntervalType getIntervalType() {
      return intervalType;
   }

   public String getTime() {
      return time;
   }
}
