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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;
import com.google.gson.annotations.SerializedName;

/**
 * Volume Type used in the Volume Type Extension for Nova
 *
 * @see org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeClient
 */
public class VolumeType {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromVolumeType(this);
   }

   public static abstract class Builder<T extends Builder<T>> {
      protected abstract T self();

      private String id;
      private String name;
      private Date created = new Date();
      private Date updated;
      private Map<String, String> extraSpecs;

      /**
       * @see VolumeType#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see VolumeType#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see VolumeType#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see VolumeType#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      /**
       * @see VolumeType#getExtraSpecs()
       */
      public T extraSpecs(Map<String, String> extraSpecs) {
         this.extraSpecs = ImmutableMap.copyOf(extraSpecs);
         return self();
      }

      public VolumeType build() {
         return new VolumeType(this);
      }

      public T fromVolumeType(VolumeType in) {
         return this
               .id(in.getId())
               .name(in.getName())
               .extraSpecs(in.getExtraSpecs())
               .created(in.getCreated())
               .updated(in.getUpdated().orNull());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected VolumeType() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   private String id;
   private String name;
   @SerializedName("created_at")
   private Date created;
   @SerializedName("updated_at")
   private Optional<Date> updated = Optional.absent();
   @SerializedName(value = "extra_specs")
   private Map<String, String> extraSpecs = ImmutableMap.of();

   protected VolumeType(Builder<?> builder) {
      this.id = checkNotNull(builder.id, "id");
      this.name = checkNotNull(builder.name, "name");
      this.extraSpecs = checkNotNull(builder.extraSpecs, "extraSpecs");
      this.created = checkNotNull(builder.created, "created");
      this.updated = Optional.fromNullable(builder.updated);
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   /** The Date the VolumeType was created */
   public Date getCreated() {
      return created;
   }

   /** The Date the VolumeType as last updated - absent if no updates have taken place */
   public Optional<Date> getUpdated() {
      return updated;
   }

   public Map<String, String> getExtraSpecs() {
      return Collections.unmodifiableMap(this.extraSpecs);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, created, updated, extraSpecs);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VolumeType that = VolumeType.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.created, that.created)
            && Objects.equal(this.updated, that.updated)
            && Objects.equal(this.extraSpecs, that.extraSpecs);
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("id", id)
            .add("name", name)
            .add("created", created)
            .add("updated", updated)
            .add("extraSpecs", extraSpecs);
   }

   @Override
   public String toString() {
      return string().toString();
   }

}