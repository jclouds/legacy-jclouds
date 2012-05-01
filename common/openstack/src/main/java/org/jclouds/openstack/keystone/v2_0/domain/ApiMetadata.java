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
import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

/**
 * Class ApiMetadata
 */
public class ApiMetadata extends Resource {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public static class Builder extends Resource.Builder {
      private String status;
      private Date updated;
      private Set<MediaType> mediaTypes = Sets.newLinkedHashSet();

      public Builder status(String status) {
         this.status = status;
         return this;
      }

      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      public Builder mediaTypes(Set<MediaType> mediaTypes) {
         this.mediaTypes = mediaTypes;
         return this;
      }

      public ApiMetadata build() {
         return new ApiMetadata(id, name, links, updated, status, mediaTypes);
      }
      
      public Builder fromApiMetadata(ApiMetadata in) {
         return fromResource(in)
               .status(in.getStatus())
               .updated(in.getUpdated())
               .mediaTypes(in.getMediaTypes());
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(Resource in) {
         return Builder.class.cast(super.fromResource(in));
      }
   }
   
   private final String status;
   private final Date updated;
   @SerializedName("media-types")
   private final Set<MediaType> mediaTypes;

   protected ApiMetadata(String id, String name, Set<Link> links, Date updated, String status, Set<MediaType> mediaTypes) {
      super(id, name, links);
      this.status = status;
      this.updated = updated;
      this.mediaTypes = ImmutableSet.copyOf(checkNotNull(mediaTypes, "mediaTypes"));
   }

   /**
    */
   @Nullable
   public String getStatus() {
      return this.status;
   }

   /**
    */
   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   /**
    */
   @Nullable
   public Set<MediaType> getMediaTypes() {
      return Collections.unmodifiableSet(this.mediaTypes);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(status, updated, mediaTypes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      ApiMetadata that = ApiMetadata.class.cast(obj);
      return Objects.equal(this.status, that.status)
            && Objects.equal(this.updated, that.updated)
            && Objects.equal(this.mediaTypes, that.mediaTypes)
            ;
   }

   protected ToStringHelper string() {
      return super.string()
            .add("status", status)
            .add("updated", updated)
            .add("mediaTypes", mediaTypes);
   }

}