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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Volume Type used in the Volume Type Extension for Nova
 * 
 * @see org.jclouds.openstack.nova.v2_0.extensions.VolumeTypeApi
*/
public class VolumeType {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromVolumeType(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String id;
      protected String name;
      protected Date created;
      protected Date updated;
      protected Map<String, String> extraSpecs = ImmutableMap.of();
   
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
         this.extraSpecs = ImmutableMap.copyOf(checkNotNull(extraSpecs, "extraSpecs"));     
         return self();
      }

      public VolumeType build() {
         return new VolumeType(id, name, created, updated, extraSpecs);
      }
      
      public T fromVolumeType(VolumeType in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .created(in.getCreated())
                  .updated(in.getUpdated().orNull())
                  .extraSpecs(in.getExtraSpecs());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String name;
   @Named("created_at")
   private final Date created;
   @Named("updated_at")
   private final Optional<Date> updated;
   @Named("extra_specs")
   private final Map<String, String> extraSpecs;

   @ConstructorProperties({
      "id", "name", "created_at", "updated_at", "extra_specs"
   })
   protected VolumeType(String id, String name, Date created, @Nullable Date updated, Map<String, String> extraSpecs) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.created = checkNotNull(created, "created");
      this.updated = Optional.fromNullable(updated);
      this.extraSpecs = ImmutableMap.copyOf(checkNotNull(extraSpecs, "extraSpecs"));     
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   /**
    * The Date the VolumeType was created
    */
   public Date getCreated() {
      return this.created;
   }

   /**
    * The Date the VolumeType as last updated - absent if no updates have taken place
    */
   public Optional<Date> getUpdated() {
      return this.updated;
   }

   public Map<String, String> getExtraSpecs() {
      return this.extraSpecs;
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
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("created", created).add("updated", updated).add("extraSpecs", extraSpecs);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
