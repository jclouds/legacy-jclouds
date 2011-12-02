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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class Snapshot implements Comparable<Snapshot> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private long id;
      private String account;
      private Date created;
      private String domain;
      private long domainId;
      private Interval interval;
      private long jobId;
      private String jobStatus;
      private String name;
      private Type snapshotType;
      private State state;
      private long volumeId;
      private String volumeName;
      private Volume.VolumeType volumeType;

      /**
       * @param id ID of the snapshot
       */
      public Builder id(long id) {
         this.id = id;
         return this;
      }

      /**
       * @param account the account associated with the snapshot
       */
      public Builder account(String account) {
         this.account = account;
         return this;
      }

      /**
       * @param created the date the snapshot was created
       */
      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      /**
       * @param domain the domain name of the snapshot's account
       */
      public Builder domain(String domain) {
         this.domain = domain;
         return this;
      }

      /**
       * @param domainId the domain ID of the snapshot's account
       */
      public Builder domainId(long domainId) {
         this.domainId = domainId;
         return this;
      }

      /**
       * @param interval valid types are hourly, daily, weekly, monthy, template, and none.
       */
      public Builder interval(Interval interval) {
         this.interval = interval;
         return this;
      }

      /**
       * @param jobId the job ID associated with the snapshot. This is only displayed if the snapshot listed is part of a currently running asynchronous job.
       */
      public Builder jobId(long jobId) {
         this.jobId = jobId;
         return this;
      }

      /**
       * @param jobStatus the job status associated with the snapshot. This is only displayed if the snapshot listed is part of a currently running asynchronous job.
       */
      public Builder jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return this;
      }

      /**
       * @param name name of the snapshot
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @param snapshotType the type of the snapshot
       */
      public Builder snapshotType(Type snapshotType) {
         this.snapshotType = snapshotType;
         return this;
      }

      /**
       * @param state the state of the snapshot. BackedUp means that snapshot is ready to be used; Creating - the snapshot is being allocated on the primary storage; BackingUp - the snapshot is being backed up on secondary storage
       */
      public Builder state(State state) {
         this.state = state;
         return this;
      }

      /**
       * @param volumeId ID of the disk volume
       */
      public Builder volumeId(long volumeId) {
         this.volumeId = volumeId;
         return this;
      }

      /**
       * @param volumeName name of the disk volume
       */
      public Builder volumeName(String volumeName) {
         this.volumeName = volumeName;
         return this;
      }

      /**
       * @param volumeType type of the disk volume
       */
      public Builder volumeType(Volume.VolumeType volumeType) {
         this.volumeType = volumeType;
         return this;
      }

      public Snapshot build() {
         return new Snapshot(id, account, created, domain, domainId, interval, jobId,
               jobStatus, name, snapshotType, state, volumeId, volumeName, volumeType);
      }

   }

   public enum State {

      BackedUp, Creating, BackingUp, UNRECOGNIZED;

      public static State fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public enum Type {

      MANUAL, RECURRING, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public enum Interval {

      HOURLY, DAILY, WEEKLY, MONTHLY, template, none, UNRECOGNIZED;

      public static Interval fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   private long id;
   private String account;
   private Date created;
   private String domain;
   @SerializedName("domainid")
   private long domainId;
   @SerializedName("intervaltype")
   private Interval interval;
   @SerializedName("jobid")
   private long jobId;
   @SerializedName("jobstatus")
   private String jobStatus;
   private String name;
   @SerializedName("snapshottype")
   private Type snapshotType;
   private State state;
   @SerializedName("volumeid")
   private long volumeId;
   @SerializedName("volumename")
   private String volumeName;
   @SerializedName("volumetype")
   private Volume.VolumeType volumeType;

   public Snapshot(long id, String account, Date created, String domain, long domainId, Interval interval, long jobId,
         String jobStatus, String name, Type snapshotType, State state, long volumeId, String volumeName, Volume.VolumeType volumeType) {
      this.id = id;
      this.account = account;
      this.created = created;
      this.domain = domain;
      this.domainId = domainId;
      this.interval = interval;
      this.jobId = jobId;
      this.jobStatus = jobStatus;
      this.name = name;
      this.snapshotType = snapshotType;
      this.state = state;
      this.volumeId = volumeId;
      this.volumeName = volumeName;
      this.volumeType = volumeType;
   }

   /**
    * present only for serializer
    */
   Snapshot() {
   }

   /**
    * @return ID of the snapshot
    */
   public long getId() {
      return id;
   }

   /**
    * @return the account associated with the snapshot
    */
   public String getAccount() {
      return account;
   }

   /**
    * @return the date the snapshot was created
    */
   public Date getCreated() {
      return created;
   }

   /**
    * @return the domain name of the snapshot's account
    */
   public String getDomain() {
      return domain;
   }

   /**
    * @return the domain ID of the snapshot's account
    */
   public long getDomainId() {
      return domainId;
   }

   /**
    * @return valid types are hourly, daily, weekly, monthy, template, and none.
    */
   public Interval getInterval() {
      return interval;
   }

   /**
    * @return the job ID associated with the snapshot. This is only displayed if the snapshot listed is part of a currently running asynchronous job.
    */
   public long getJobId() {
      return jobId;
   }

   /**
    * @return the job status associated with the snapshot. This is only displayed if the snapshot listed is part of a currently running asynchronous job.
    */
   public String getJobStatus() {
      return jobStatus;
   }

   /**
    * @return name of the snapshot
    */
   public String getName() {
      return name;
   }

   /**
    * @return the type of the snapshot
    */
   public Type getSnapshotType() {
      return snapshotType;
   }

   /**
    * @return the state of the snapshot. BackedUp means that snapshot is ready to be used; Creating - the snapshot is being allocated on the primary storage; BackingUp - the snapshot is being backed up on secondary storage
    */
   public State getState() {
      return state;
   }

   /**
    * @return ID of the disk volume
    */
   public long getVolumeId() {
      return volumeId;
   }

   /**
    * @return name of the disk volume
    */
   public String getVolumeName() {
      return volumeName;
   }

   /**
    * @return type of the disk volume
    */
   public Volume.VolumeType getVolumeType() {
      return volumeType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Snapshot snapshot = (Snapshot) o;

      if (domainId != snapshot.domainId) return false;
      if (id != snapshot.id) return false;
      if (jobId != snapshot.jobId) return false;
      if (volumeId != snapshot.volumeId) return false;
      if (account != null ? !account.equals(snapshot.account) : snapshot.account != null) return false;
      if (created != null ? !created.equals(snapshot.created) : snapshot.created != null) return false;
      if (domain != null ? !domain.equals(snapshot.domain) : snapshot.domain != null) return false;
      if (interval != snapshot.interval) return false;
      if (jobStatus != null ? !jobStatus.equals(snapshot.jobStatus) : snapshot.jobStatus != null) return false;
      if (name != null ? !name.equals(snapshot.name) : snapshot.name != null) return false;
      if (snapshotType != snapshot.snapshotType) return false;
      if (state != snapshot.state) return false;
      if (volumeName != null ? !volumeName.equals(snapshot.volumeName) : snapshot.volumeName != null) return false;
      if (volumeType != null ? !volumeType.equals(snapshot.volumeType) : snapshot.volumeType != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = (int) (id ^ (id >>> 32));
      result = 31 * result + (account != null ? account.hashCode() : 0);
      result = 31 * result + (created != null ? created.hashCode() : 0);
      result = 31 * result + (domain != null ? domain.hashCode() : 0);
      result = 31 * result + (int) (domainId ^ (domainId >>> 32));
      result = 31 * result + (interval != null ? interval.hashCode() : 0);
      result = 31 * result + (int) (jobId ^ (jobId >>> 32));
      result = 31 * result + (jobStatus != null ? jobStatus.hashCode() : 0);
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (snapshotType != null ? snapshotType.hashCode() : 0);
      result = 31 * result + (state != null ? state.hashCode() : 0);
      result = 31 * result + (int) (volumeId ^ (volumeId >>> 32));
      result = 31 * result + (volumeName != null ? volumeName.hashCode() : 0);
      result = 31 * result + (volumeType != null ? volumeType.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "Snapshot[" +
            "id=" + id +
            ", account='" + account + '\'' +
            ", created=" + created +
            ", domain='" + domain + '\'' +
            ", domainId=" + domainId +
            ", interval=" + interval +
            ", jobId=" + jobId +
            ", jobStatus='" + jobStatus + '\'' +
            ", name='" + name + '\'' +
            ", snapshotType=" + snapshotType +
            ", state=" + state +
            ", volumeId=" + volumeId +
            ", volumeName='" + volumeName + '\'' +
            ", volumeType='" + volumeType + '\'' +
            ']';
   }

   @Override
   public int compareTo(Snapshot other) {
      return new Long(this.id).compareTo(other.getId());
   }
}
