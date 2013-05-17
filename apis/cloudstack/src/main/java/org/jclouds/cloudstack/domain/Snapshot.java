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
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class Snapshot
 *
 * @author Richard Downer
 */
public class Snapshot {

   /**
    */
   public static enum State {

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

   /**
    */
   public static enum Type {

      MANUAL, RECURRING, UNRECOGNIZED;

      public static Type fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   /**
    */
   public static enum Interval {

      HOURLY, DAILY, WEEKLY, MONTHLY, template, none, UNRECOGNIZED;

      public static Interval fromValue(String type) {
         try {
            return valueOf(checkNotNull(type, "type"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSnapshot(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String id;
      protected String account;
      protected Date created;
      protected String domain;
      protected String domainId;
      protected Snapshot.Interval interval;
      protected String jobId;
      protected String jobStatus;
      protected String name;
      protected Snapshot.Type snapshotType;
      protected Snapshot.State state;
      protected String volumeId;
      protected String volumeName;
      protected Volume.Type volumeType;

      /**
       * @see Snapshot#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see Snapshot#getAccount()
       */
      public T account(String account) {
         this.account = account;
         return self();
      }

      /**
       * @see Snapshot#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see Snapshot#getDomain()
       */
      public T domain(String domain) {
         this.domain = domain;
         return self();
      }

      /**
       * @see Snapshot#getDomainId()
       */
      public T domainId(String domainId) {
         this.domainId = domainId;
         return self();
      }

      /**
       * @see Snapshot#getInterval()
       */
      public T interval(Snapshot.Interval interval) {
         this.interval = interval;
         return self();
      }

      /**
       * @see Snapshot#getJobId()
       */
      public T jobId(String jobId) {
         this.jobId = jobId;
         return self();
      }

      /**
       * @see Snapshot#getJobStatus()
       */
      public T jobStatus(String jobStatus) {
         this.jobStatus = jobStatus;
         return self();
      }

      /**
       * @see Snapshot#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Snapshot#getSnapshotType()
       */
      public T snapshotType(Snapshot.Type snapshotType) {
         this.snapshotType = snapshotType;
         return self();
      }

      /**
       * @see Snapshot#getState()
       */
      public T state(Snapshot.State state) {
         this.state = state;
         return self();
      }

      /**
       * @see Snapshot#getVolumeId()
       */
      public T volumeId(String volumeId) {
         this.volumeId = volumeId;
         return self();
      }

      /**
       * @see Snapshot#getVolumeName()
       */
      public T volumeName(String volumeName) {
         this.volumeName = volumeName;
         return self();
      }

      /**
       * @see Snapshot#getVolumeType()
       */
      public T volumeType(Volume.Type volumeType) {
         this.volumeType = volumeType;
         return self();
      }

      public Snapshot build() {
         return new Snapshot(id, account, created, domain, domainId, interval, jobId, jobStatus, name, snapshotType, state,
               volumeId, volumeName, volumeType);
      }

      public T fromSnapshot(Snapshot in) {
         return this
               .id(in.getId())
               .account(in.getAccount())
               .created(in.getCreated())
               .domain(in.getDomain())
               .domainId(in.getDomainId())
               .interval(in.getInterval())
               .jobId(in.getJobId())
               .jobStatus(in.getJobStatus())
               .name(in.getName())
               .snapshotType(in.getSnapshotType())
               .state(in.getState())
               .volumeId(in.getVolumeId())
               .volumeName(in.getVolumeName())
               .volumeType(in.getVolumeType());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String account;
   private final Date created;
   private final String domain;
   private final String domainId;
   private final Snapshot.Interval interval;
   private final String jobId;
   private final String jobStatus;
   private final String name;
   private final Snapshot.Type snapshotType;
   private final Snapshot.State state;
   private final String volumeId;
   private final String volumeName;
   private final Volume.Type volumeType;

   @ConstructorProperties({
         "id", "account", "created", "domain", "domainid", "intervaltype", "jobid", "jobstatus", "name", "snapshottype", "state", "volumeid", "volumename", "volumetype"
   })
   protected Snapshot(String id, @Nullable String account, @Nullable Date created, @Nullable String domain, @Nullable String domainId,
                      @Nullable Snapshot.Interval interval, @Nullable String jobId, @Nullable String jobStatus, @Nullable String name,
                      @Nullable Snapshot.Type snapshotType, @Nullable Snapshot.State state, @Nullable String volumeId, @Nullable String volumeName,
                      @Nullable Volume.Type volumeType) {
      this.id = checkNotNull(id, "id");
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
    * @return ID of the snapshot
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the account associated with the snapshot
    */
   @Nullable
   public String getAccount() {
      return this.account;
   }

   /**
    * @return the date the snapshot was created
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the domain name of the snapshot's account
    */
   @Nullable
   public String getDomain() {
      return this.domain;
   }

   /**
    * @return the domain ID of the snapshot's account
    */
   @Nullable
   public String getDomainId() {
      return this.domainId;
   }

   /**
    * @return valid types are hourly, daily, weekly, monthly, template, and none.
    */
   @Nullable
   public Snapshot.Interval getInterval() {
      return this.interval;
   }

   /**
    * @return the job ID associated with the snapshot. This is only displayed if the snapshot listed is part of a currently running asynchronous job.
    */
   @Nullable
   public String getJobId() {
      return this.jobId;
   }

   /**
    * @return the job status associated with the snapshot. This is only displayed if the snapshot listed is part of a currently running asynchronous job.
    */
   @Nullable
   public String getJobStatus() {
      return this.jobStatus;
   }

   /**
    * @return name of the snapshot
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the type of the snapshot
    */
   @Nullable
   public Snapshot.Type getSnapshotType() {
      return this.snapshotType;
   }

   /**
    * @return the state of the snapshot. BackedUp means that snapshot is ready to be used; Creating - the snapshot is being allocated on the primary storage; BackingUp - the snapshot is being backed up on secondary storage
    */
   @Nullable
   public Snapshot.State getState() {
      return this.state;
   }

   /**
    * @return ID of the disk volume
    */
   @Nullable
   public String getVolumeId() {
      return this.volumeId;
   }

   /**
    * @return name of the disk volume
    */
   @Nullable
   public String getVolumeName() {
      return this.volumeName;
   }

   /**
    * @return type of the disk volume
    */
   @Nullable
   public Volume.Type getVolumeType() {
      return this.volumeType;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, account, created, domain, domainId, interval, jobId, jobStatus, name, snapshotType, state, volumeId, volumeName, volumeType);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Snapshot that = Snapshot.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.account, that.account)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.domain, that.domain)
            && Objects.equal(this.domainId, that.domainId)
            && Objects.equal(this.interval, that.interval)
            && Objects.equal(this.jobId, that.jobId)
            && Objects.equal(this.jobStatus, that.jobStatus)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.snapshotType, that.snapshotType)
            && Objects.equal(this.state, that.state)
            && Objects.equal(this.volumeId, that.volumeId)
            && Objects.equal(this.volumeName, that.volumeName)
            && Objects.equal(this.volumeType, that.volumeType);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("account", account).add("created", created).add("domain", domain).add("domainId", domainId)
            .add("interval", interval).add("jobId", jobId).add("jobStatus", jobStatus).add("name", name).add("snapshotType", snapshotType)
            .add("state", state).add("volumeId", volumeId).add("volumeName", volumeName).add("volumeType", volumeType);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}
