package org.jclouds.cloudstack.domain;

/**
 * Describes the schedule of a snapshot policy.
 *
 * @author Richard Downer
 */
public class SnapshotPolicySchedule {

   public static SnapshotPolicySchedule hourly(int minute) {
      return new SnapshotPolicySchedule(Snapshot.Interval.HOURLY, "FIXME");
   }

   public static SnapshotPolicySchedule daily(int hour, int minute) {
      return new SnapshotPolicySchedule(Snapshot.Interval.DAILY, "FIXME");
   }

   public static SnapshotPolicySchedule weekly(int day, int hour, int minute) {
      return new SnapshotPolicySchedule(Snapshot.Interval.WEEKLY, "FIXME");
   }

   public static SnapshotPolicySchedule monthly(int day, int hour, int minute) {
      return new SnapshotPolicySchedule(Snapshot.Interval.MONTHLY, String.format("%02d:%02d:%02d", minute, hour, day));
   }

   private Snapshot.Interval interval;
   private String time;

   private SnapshotPolicySchedule(Snapshot.Interval interval, String time) {
      this.interval = interval;
      this.time = time;
   }

   public Snapshot.Interval getInterval() {
      return interval;
   }

   public String getTime() {
      return time;
   }
}
