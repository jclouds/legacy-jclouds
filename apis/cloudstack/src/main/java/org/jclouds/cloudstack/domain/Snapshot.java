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

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.gson.annotations.SerializedName;

/**
 * @author Richard Downer
 */
public class Snapshot implements Comparable<Snapshot> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String id;
      private String account;
      private Date created;
      private String domain;
      private String domainId;
      private Interval interval;
      private String jobId;
      private String jobStatus;
      private String name;
      private Type snapshotType;
      private State state;
      private String volumeId;
      private String volumeName;
      private Volume.Type volumeType;

      /**
       * @param id ID of the snapshot
       */
      public Builder id(String id) {
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
      public Builder domainId(String domainId) {
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
      public Builder jobId(String jobId) {
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
      public Builder volumeId(String volumeId) {
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
      public Builder volumeType(Volume.Type volumeType) {
         this.volumeType = volumeType;
         return this;
      }

      public Snapshot build() {
         return new Snapshot(id, account, created, domain, domainId, interval, jobId,
               jobStatus, name, snapshotType, state, volumeId, volumeName, volumeType);
      }

   }

   public enum State {

      BACKED_UP, CREATING, BACKING_UP, UNRECOGNIZED;

      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static State fromValue(String state) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(state, "state")));
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

   private String id;
   private String account;
   private Date created;
   private String domain;
   @SerializedName("domainid")
   private String domainId;
   @SerializedName("intervaltype")
   private Interval interval;
   @SerializedName("jobid")
   private String jobId;
   @SerializedName("jobstatus")
   private String jobStatus;
   private String name;
   @SerializedName("snapshottype")
   private Type snapshotType;
   private State state;
   @SerializedName("volumeid")
   private String volumeId;
   @SerializedName("volumename")
   private String volumeName;
   @SerializedName("volumetype")
   private Volume.Type volumeType;

   public Snapshot(String id, String account, Date created, String domain, String domainId, Interval interval, String jobId,
         String jobStatus, String name, Type snapshotType, State state, String volumeId, String volumeName, Volume.Type volumeType) {
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
   public String getId() {
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
   public String getDomainId() {
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
   public String getJobId() {
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
   public String getVolumeId() {
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
   public Volume.Type getVolumeType() {
      return volumeType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Snapshot that = (Snapshot) o;

      if (!Objects.equal(domainId, that.domainId)) return false;
      if (!Objects.equal(id, that.id)) return false;
      if (!Objects.equal(jobId, that.jobId)) return false;
      if (!Objects.equal(volumeId, that.volumeId)) return false;
      if (!Objects.equal(account, that.account)) return false;
      if (!Objects.equal(created, that.created)) return false;
      if (!Objects.equal(domain, that.domain)) return false;
      if (!Objects.equal(interval, that.interval)) return false;
      if (!Objects.equal(jobStatus, that.jobStatus)) return false;
      if (!Objects.equal(name, that.name)) return false;
      if (!Objects.equal(snapshotType, that.snapshotType)) return false;
      if (!Objects.equal(state, that.state)) return false;
      if (!Objects.equal(volumeName, that.volumeName)) return false;
      if (!Objects.equal(volumeType, that.volumeType)) return false;

      return true;
   }

   @Override
   public int hashCode() {
       return Objects.hashCode(domainId, id, jobId, volumeId, account, created, domain,
                               interval, jobStatus, name, snapshotType, state, volumeName,
                               volumeType);
   }

   @Override
   public String toString() {
      return "Snapshot{" +
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
            ", volumeType=" + volumeType +
            '}';
   }

   @Override
   public int compareTo(Snapshot other) {
      return id.compareTo(other.getId());
   }
}
