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
 * A persistent snapshot resource
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/snapshots"/>
 */
public class Snapshot extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSnapshot(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private long diskSizeGb;
      private String status;
      private String sourceDisk;
      private String sourceDiskId;

      /**
       * @see Snapshot#getDiskSizeGb
       */
      public T diskSizeGb(long diskSizeGb) {
         this.diskSizeGb = diskSizeGb;
         return self();
      }

      /**
       * @see Snapshot#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see Snapshot#getSourceSnapshot()
       */
      public T sourceDisk(String sourceDisk) {
         this.sourceDisk = sourceDisk;
         return self();
      }

      /**
       * @see Snapshot#getSourceSnapshotId()
       */
      public T sourceDiskId(String sourceDiskId) {
         this.sourceDiskId = sourceDiskId;
         return self();
      }


      public Snapshot build() {
         return new Snapshot(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, diskSizeGb, status, sourceDisk, sourceDiskId);
      }

      public T fromSnapshot(Snapshot in) {
         return super.fromResource(in);
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final long diskSizeGb;
   private final String status;
   private final String sourceDisk;
   private final String sourceDiskId;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "diskSizeGb",
           "status", "sourceDisk", "sourceDiskId"
   })
   protected Snapshot(String id, Date creationTimestamp, String selfLink, String name, String description,
                      long diskSizeGb, String status, String sourceDisk, String sourceDiskId) {
      super(Kind.SNAPSHOT, id, creationTimestamp, selfLink, name, description);
      this.diskSizeGb = diskSizeGb;
      this.status = status;
      this.sourceDisk = sourceDisk;
      this.sourceDiskId = sourceDiskId;
   }

   /**
    * @return size of the persistent disk snapshot, specified in GB (output only).
    */
   @Nullable
   public long getDiskSizeGb() {
      return diskSizeGb;
   }

   /**
    * @return the status of the persistent disk snapshot (output only).
    */
   @Nullable
   public String getStatus() {
      return status;
   }

   /**
    * @return the source disk used to create this snapshot. Once the source disk has been deleted from the system,
    *         this field will be cleared, and will not be set even if a disk with the same name has been re-created.
    */
   @Nullable
   public String getSourceSnapshot() {
      return sourceDisk;
   }

   /**
    * @return The 'id' value of the disk used to create this snapshot. This value may be used to determine whether
    *         the snapshot was taken from the current or a previous instance of a given disk name.
    */
   public String getSourceSnapshotId() {
      return sourceDiskId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, diskSizeGb,
              sourceDisk, sourceDiskId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Snapshot that = Snapshot.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.diskSizeGb, that.diskSizeGb)
              && Objects.equal(this.sourceDisk, that.sourceDisk)
              && Objects.equal(this.sourceDiskId, that.sourceDiskId);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("diskSizeGb", diskSizeGb).add("sourceDisk", sourceDisk).add("sourceDiskId", sourceDiskId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

}

