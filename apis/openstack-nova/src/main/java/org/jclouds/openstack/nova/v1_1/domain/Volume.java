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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * An Openstack Nova Volume
 */
public class Volume {

   public static enum Status {
      CREATING, AVAILABLE, IN_USE, DELETING, ERROR, UNRECOGNIZED;
      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVolume(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String id;
      private Status status;
      private int size;
      private String zone;
      private Date created;
      private Set<VolumeAttachment> attachments = Sets.newLinkedHashSet();
      private String volumeType;
      private String snapshotId;
      private String name;
      private String description;
      private Map<String, String> metadata = Maps.newHashMap();

      public T id(String id) {
         this.id = id;
         return self();
      }

      public T status(Status status) {
         this.status = status;
         return self();
      }

      public T size(int size) {
         this.size = size;
         return self();
      }

      public T zone(String zone) {
         this.zone = zone;
         return self();
      }

      public T created(Date created) {
         this.created = created;
         return self();
      }

      public T attachments(Set<VolumeAttachment> attachments) {
         this.attachments = attachments;
         return self();
      }

      public T volumeType(String volumeType) {
         this.volumeType = volumeType;
         return self();
      }

      public T snapshotId(String snapshotId) {
         this.snapshotId = snapshotId;
         return self();
      }

      public T metadata(Map<String, String> metadata) {
         this.metadata = metadata;
         return self();
      }

      public T name(String name) {
         this.name = name;
         return self();
      }

      public T description(String description) {
         this.description = description;
         return self();
      }
      
      public Volume build() {
         return new Volume(this);
      }

      public T fromVolume(Volume in) {
         return this
               .id(in.getId())
               .status(in.getStatus())
               .size(in.getSize())
               .zone(in.getZone())
               .created(in.getCreated())
               .attachments(in.getAttachments())
               .volumeType(in.getVolumeType())
               .snapshotId(in.getSnapshotId())
               .metadata(in.getMetadata())
               ;
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final Status status;
   private final int size;
   @SerializedName(value="availabilityZone")
   private final String zone;
   @SerializedName(value="createdAt")
   private final Date created;
   private final Set<VolumeAttachment> attachments;
   private final String volumeType;   
   private final String snapshotId;
   @SerializedName(value="displayName")
   private final String name;
   @SerializedName(value="displayDescription")
   private final String description;
   private final Map<String, String> metadata;

   protected Volume(Builder<?> builder) {
      this.id = builder.id;
      this.status = builder.status;
      this.size = builder.size;
      this.zone = builder.zone;
      this.created = builder.created;
      this.attachments = ImmutableSet.copyOf(checkNotNull(builder.attachments, "attachments"));
      this.volumeType = builder.volumeType;
      this.snapshotId = builder.snapshotId;
      this.name = builder.name;
      this.description = builder.description;
      this.metadata = ImmutableMap.copyOf(checkNotNull(builder.metadata, "metadata"));
   }

   /**
    * @return the id of this volume
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the status of this volume
    */
   public Status getStatus() {
      return this.status;
   }

   /**
    * @return the size in GB of this volume
    */
   public int getSize() {
      return this.size;
   }

   /**
    * @return the availabilityZone containing this volume
    */
   public String getZone() {
      return this.zone;
   }

   /**
    * @return the time this volume was created
    */
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return the set of attachments (to Servers)
    */
   @Nullable
   public Set<VolumeAttachment> getAttachments() {
      return Collections.unmodifiableSet(this.attachments);
   }

   /**
    * @return the type of this volume
    */
   @Nullable
   public String getVolumeType() {
      return this.volumeType;
   }

   /**
    * @return the snapshot id this volume is associated with.
    */
   @Nullable
   public String getSnapshotId() {
      return this.snapshotId;
   }

   /**
    * @return the name of this volume - as displayed in the openstack console
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return the description of this volume - as displayed in the openstack console
    */
   @Nullable
   public String getDescription() {
      return this.description;
   }
   
   @Nullable
   public Map<String, String> getMetadata() {
      return Collections.unmodifiableMap(this.metadata);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, status, size, zone, created, attachments, volumeType, snapshotId, name, description, metadata);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Volume that = Volume.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.status, that.status)
            && Objects.equal(this.size, that.size)
            && Objects.equal(this.zone, that.zone)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.attachments, that.attachments)
            && Objects.equal(this.volumeType, that.volumeType)
            && Objects.equal(this.snapshotId, that.snapshotId)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.description, that.description)
            && Objects.equal(this.metadata, that.metadata)
            ;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("id", id)
            .add("status", status)
            .add("size", size)
            .add("zone", zone)
            .add("created", created)
            .add("attachments", attachments)
            .add("volumeType", volumeType)
            .add("snapshotId", snapshotId)
            .add("name", name)
            .add("description", description)
            .add("metadata", metadata)
            ;
   }

   @Override
   public String toString() {
      return string().toString();
   }

}