/*
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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;

/**
 * A persistent disk resource
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/disks"/>
 */
public class Disk extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDisk(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private long sizeGb;
      private String zone;
      private String options;
      private String status;
      private String sourceSnapshot;
      private String sourceSnapshotId;

      /**
       * @see Disk#getSizeGb()
       */
      public T sizeGb(long sizeGb) {
         this.sizeGb = sizeGb;
         return self();
      }

      /**
       * @see Disk#getZone()
       */
      public T zone(String zone) {
         this.zone = zone;
         return self();
      }

      /**
       * @see Disk#getOptions()
       */
      public T options(String options) {
         this.options = options;
         return self();
      }

      /**
       * @see Disk#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see Disk#getSourceSnapshot()
       */
      public T sourceSnapshot(String sourceSnapshot) {
         this.sourceSnapshot = sourceSnapshot;
         return self();
      }

      /**
       * @see Disk#getSourceSnapshotId()
       */
      public T sourceSnapshotId(String sourceSnapshotId) {
         this.sourceSnapshotId = sourceSnapshotId;
         return self();
      }


      public Disk build() {
         return new Disk(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, sizeGb, zone, options, status, sourceSnapshot, sourceSnapshotId);
      }

      public T fromDisk(Disk in) {
         return super.fromResource(in);
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long sizeGb;
   private final String zone;
   private final String options;
   private final String status;
   private final String sourceSnapshot;
   private final String sourceSnapshotId;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "sizeGb", "zone",
           "options", "status", "sourceSnapshot", "sourceSnapshotId"
   })
   protected Disk(String id, Date creationTimestamp, String selfLink, String name, String description,
                  long sizeGb, String zone, String options, String status, String sourceSnapshot,
                  String sourceSnapshotId) {
      super(Kind.DISK, id, creationTimestamp, selfLink, name, description);
      this.sizeGb = sizeGb;
      this.zone = zone;
      this.options = options;
      this.status = status;
      this.sourceSnapshot = sourceSnapshot;
      this.sourceSnapshotId = sourceSnapshotId;
   }

   /**
    * @return size of the persistent disk, specified in GB. Required only if sourceSnapshot is not specified.
    */
   @Nullable
   public long getSizeGb() {
      return sizeGb;
   }

   /**
    * @return URL for the zone where the persistent disk resides; provided by the client when the disk is created. A
    *         persistent disk must reside in the same zone as the instance to which it is attached.
    */
   public String getZone() {
      return zone;
   }

   /**
    * @return Internal use only.
    */
   public String getOptions() {
      return options;
   }

   /**
    * @return the status of disk creation (output only).
    */
   @Nullable
   public String getStatus() {
      return status;
   }

   /**
    * @return the source snapshot used to create this disk. Once the source snapshot has been deleted from the
    *         system, this field will be cleared, and will not be set even if a snapshot with the same name has been
    *         re-created (output only).
    */
   @Nullable
   public String getSourceSnapshot() {
      return sourceSnapshot;
   }

   /**
    * @return The 'id' value of the snapshot used to create this disk. This value may be used to determine whether
    *         the disk was created from the current or a previous instance of a given disk snapshot.
    */
   public String getSourceSnapshotId() {
      return sourceSnapshotId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, sizeGb, zone,
              options, sourceSnapshot, sourceSnapshotId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Disk that = Disk.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.sizeGb, that.sizeGb)
              && Objects.equal(this.zone, that.zone)
              && Objects.equal(this.options, that.options)
              && Objects.equal(this.sourceSnapshot, that.sourceSnapshot)
              && Objects.equal(this.sourceSnapshotId, that.sourceSnapshotId);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("sizeGb", sizeGb).add("zone",
                      zone).add("options", options).add("sourceSnapshot", sourceSnapshot).add("sourceSnapshotId",
                      sourceSnapshotId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

}
