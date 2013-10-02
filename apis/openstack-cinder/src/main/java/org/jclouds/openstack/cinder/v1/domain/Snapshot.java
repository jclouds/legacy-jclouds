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
package org.jclouds.openstack.cinder.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An Openstack Cinder Volume Snapshot.
 * 
 * @author Everett Toews
 */
public class Snapshot {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromSnapshot(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String volumeId;
      protected Volume.Status status;
      protected int size;
      protected Date created;
      protected String name;
      protected String description;
   
      /** 
       * @see Snapshot#getId()
       */
      public T id(String id) {
         this.id = id;
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
       * @see Snapshot#getStatus()
       */
      public T status(Volume.Status status) {
         this.status = status;
         return self();
      }

      /** 
       * @see Snapshot#getSize()
       */
      public T size(int size) {
         this.size = size;
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
       * @see Snapshot#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Snapshot#getDescription()
       */
      public T description(String description) {
         this.description = description;
         return self();
      }

      public Snapshot build() {
         return new Snapshot(id, volumeId, status, size, created, name, description);
      }
      
      public T fromSnapshot(Snapshot in) {
         return this
                  .id(in.getId())
                  .volumeId(in.getVolumeId())
                  .status(in.getStatus())
                  .size(in.getSize())
                  .created(in.getCreated())
                  .name(in.getName())
                  .description(in.getDescription());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   @Named("volume_id")
   private final String volumeId;
   private final Volume.Status status;
   private final int size;
   @Named("created_at")
   private final Date created;
   @Named("display_name")
   private final String name;
   @Named("display_description")
   private final String description;

   @ConstructorProperties({
      "id", "volume_id", "status", "size", "created_at", "display_name", "display_description"
   })
   protected Snapshot(String id, String volumeId, Volume.Status status, int size, @Nullable Date created, @Nullable String name, @Nullable String description) {
      this.id = checkNotNull(id, "id");
      this.volumeId = checkNotNull(volumeId, "volumeId");
      this.status = checkNotNull(status, "status");
      this.size = size;
      this.created = created;
      this.name = name;
      this.description = description;
   }

   /**
    * @return The id of this snapshot
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return The id of the Volume this snapshot was taken from
    */
   public String getVolumeId() {
      return this.volumeId;
   }

   /**
    * @return The status of this snapshot
    */
   public Volume.Status getStatus() {
      return this.status;
   }

   /**
    * @return The size in GB of the volume this snapshot was taken from
    */
   public int getSize() {
      return this.size;
   }

   /**
    * @return The data the snapshot was taken
    */
   @Nullable
   public Date getCreated() {
      return this.created;
   }

   /**
    * @return The name of this snapshot - as displayed in the openstack console
    */
   @Nullable
   public String getName() {
      return this.name;
   }

   /**
    * @return The description of this snapshot - as displayed in the openstack console
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
      Snapshot that = Snapshot.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.volumeId, that.volumeId)
               && Objects.equal(this.status, that.status)
               && Objects.equal(this.size, that.size)
               && Objects.equal(this.created, that.created)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.description, that.description);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("volumeId", volumeId).add("status", status).add("size", size).add("created", created).add("name", name).add("description", description);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
