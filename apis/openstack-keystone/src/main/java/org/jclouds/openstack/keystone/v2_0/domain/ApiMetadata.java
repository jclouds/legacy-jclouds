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
package org.jclouds.openstack.keystone.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * @author Adam Lowe
 */
public class ApiMetadata extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromApiMetadata(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      private String status;
      private Date updated;
      private Set<MediaType> mediaTypes = Sets.newLinkedHashSet();

      /**
       * @see ApiMetadata#getStatus()
       */
      public T status(String status) {
         this.status = status;
         return self();
      }

      /**
       * @see ApiMetadata#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      /**
       * @see ApiMetadata#getMediaTypes()
       */
      public T mediaTypes(Set<MediaType> mediaTypes) {
         this.mediaTypes = mediaTypes;
         return self();
      }

      public ApiMetadata build() {
         return new ApiMetadata(this);
      }

      public T fromApiMetadata(ApiMetadata in) {
         return super.fromResource(in)
               .status(in.getStatus())
               .updated(in.getUpdated())
               .mediaTypes(in.getMediaTypes());
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   protected ApiMetadata() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }

   @Nullable
   private String status;
   @Nullable
   private Date updated;


   // dealing with the goofy structure with "values" holder noted here
   // http://docs.openstack.org/api/openstack-identity-service/2.0/content/Versions-d1e472.html
   // if they change this to not be a value holder, we'll probably need to write a custom
   // deserializer.
   private static class MediaTypesHolder {
      private Set<MediaType> values = ImmutableSet.of();

      private MediaTypesHolder() {
      }

      private MediaTypesHolder(Set<MediaType> mediaTypes) {
         this.values = ImmutableSet.copyOf(checkNotNull(mediaTypes, "mediaTypes"));
      }
   }

   @SerializedName(value="media-types")
   private MediaTypesHolder mediaTypes = new MediaTypesHolder();

   protected ApiMetadata(Builder<?> builder) {
      super(builder);
      this.status = checkNotNull(builder.status, "status");
      this.updated = checkNotNull(builder.updated, "updated");
      this.mediaTypes = new MediaTypesHolder(builder.mediaTypes);
   }

   /**
    */
   public String getStatus() {
      return this.status;
   }

   /**
    */
   public Date getUpdated() {
      return this.updated;
   }

   /**
    */
   public Set<MediaType> getMediaTypes() {
      return Collections.unmodifiableSet(this.mediaTypes.values);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(status, updated, mediaTypes.values);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ApiMetadata that = ApiMetadata.class.cast(obj);
      return super.equals(that) && Objects.equal(this.status, that.status)
            && Objects.equal(this.updated, that.updated)
            && Objects.equal(this.mediaTypes.values, that.mediaTypes.values);
   }

   protected ToStringHelper string() {
      return super.string()
            .add("status", status)
            .add("updated", updated)
            .add("mediaTypes", mediaTypes.values);
   }

}