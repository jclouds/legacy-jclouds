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
package org.jclouds.openstack.nova.v1_1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.gson.annotations.SerializedName;

/**
 * An Openstack Nova Volume Snapshot
 */
public class VolumeSnapshot {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromSnapshot(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String id;
      private String volumeId;
      private Volume.Status status;
      private int size;
      private Date created;
      private String name;
      private String description;

      /** @see VolumeSnapshot#getId() */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /** @see VolumeSnapshot#getVolumeId() */
      public T volumeId(String volumeId) {
         this.volumeId = volumeId;
         return self();
      }

      /** @see VolumeSnapshot#getStatus() */
      public T status(Volume.Status status) {
         this.status = status;
         return self();
      }

      /** @see VolumeSnapshot#getSize() */
      public T size(int size) {
         this.size = size;
         return self();
      }

      /** @see VolumeSnapshot#getCreated() */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /** @see VolumeSnapshot#getName() */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** @see VolumeSnapshot#getDescription() */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public VolumeSnapshot build() {
         return new VolumeSnapshot(this);
      }

      public T fromSnapshot(VolumeSnapshot in) {
         return this
               .id(in.getId())
               .volumeId(in.getVolumeId())
               .status(in.getStatus())
               .size(in.getSize())
               .created(in.getCreated())
               .name(in.getName())
               .description(in.getDescription())
               ;
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected VolumeSnapshot() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   private String id;
   private String volumeId;
   private Volume.Status status;
   private int size;
   @SerializedName(value="createdAt")
   private Date created;
   @SerializedName(value="displayName")
   private String name;
   @SerializedName(value="displayDescription")
   private String description;

   protected VolumeSnapshot(Builder<?> builder) {
      this.id = checkNotNull(builder.id, "id");
      this.volumeId = checkNotNull(builder.volumeId, "volumeId");
      this.status = checkNotNull(builder.status, "status");
      this.size = builder.size;
      this.created = builder.created;
      this.name = builder.name;
      this.description = builder.description;
   }

   /**
    * @return the id of this snapshot
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the id of the Volume this snapshot was taken from
    */
   public String getVolumeId() {
      return this.volumeId;
   }

   /**
    * @return the status of this snapshot
    */
   public Volume.Status getStatus() {
      return this.status;
   }

   /**
    * @return the size in GB of the volume this snapshot was taken from
    */
   public int getSize() {
      return this.size;
   }

   /**
    * @return the data the snapshot was taken
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the name of this snapshot - as displayed in the openstack console
    */
   @Nullable
   public String getName() {
      return this.name;
   }


   /**
    * @return the description of this snapshot - as displayed in the openstack console
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, volumeId, status, size, created, name, description);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VolumeSnapshot that = VolumeSnapshot.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.volumeId, that.volumeId)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description)
            ;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("id", id)
            .add("volumeId", volumeId)
            .add("status", status)
            .add("size", size)
            .add("created", created)
            .add("name", name)
            .add("description", description)
            ;
   }

   @Override
   public String toString() {
      return string().toString();
   }

}