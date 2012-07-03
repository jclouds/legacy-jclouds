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
package org.jclouds.openstack.nova.v2_0.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * An image is a collection of files you use to create or rebuild a server. Operators provide
 * pre-built OS images by default. You may also create custom images.
 * 
 * @author Jeremy Daggett
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/1.1/content/Images-d1e4427.html"
      />
*/
public class Image extends Resource {

   /**
    * In-flight images will have the status attribute set to SAVING and the conditional progress
    * element (0-100% completion) will also be returned. Other possible values for the status
    * attribute include: UNKNOWN, ACTIVE, SAVING, ERROR, and DELETED. Images with an ACTIVE status
    * are available for install. The optional minDisk and minRam attributes set the minimum disk and
    * RAM requirements needed to create a server with the image.
    * 
    * @author Adrian Cole
    */
   public static enum Status {
      
      UNRECOGNIZED, UNKNOWN, ACTIVE, SAVING, ERROR, DELETED;
      
      public String value() {
      return name();
      }
      
      public static Status fromValue(String v) {
      try {
      return valueOf(v);
      } catch (IllegalArgumentException e) {
      return UNRECOGNIZED;
      }
      }
      
   }

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromImage(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      protected Date updated;
      protected Date created;
      protected String tenantId;
      protected String userId;
      protected Image.Status status;
      protected int progress;
      protected int minDisk;
      protected int minRam;
      protected Resource server;
      protected Map<String, String> metadata = ImmutableMap.of();
   
      /** 
       * @see Image#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      /** 
       * @see Image#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /** 
       * @see Image#getTenantId()
       */
      public T tenantId(String tenantId) {
         this.tenantId = tenantId;
         return self();
      }

      /** 
       * @see Image#getUserId()
       */
      public T userId(String userId) {
         this.userId = userId;
         return self();
      }

      /** 
       * @see Image#getStatus()
       */
      public T status(Image.Status status) {
         this.status = status;
         return self();
      }

      /** 
       * @see Image#getProgress()
       */
      public T progress(int progress) {
         this.progress = progress;
         return self();
      }

      /** 
       * @see Image#getMinDisk()
       */
      public T minDisk(int minDisk) {
         this.minDisk = minDisk;
         return self();
      }

      /** 
       * @see Image#getMinRam()
       */
      public T minRam(int minRam) {
         this.minRam = minRam;
         return self();
      }

      /** 
       * @see Image#getServer()
       */
      public T server(Resource server) {
         this.server = server;
         return self();
      }

      /** 
       * @see Image#getMetadata()
       */
      public T metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));     
         return self();
      }

      public Image build() {
         return new Image(id, name, links, updated, created, tenantId, userId, status, progress, minDisk, minRam, server, metadata);
      }
      
      public T fromImage(Image in) {
         return super.fromResource(in)
                  .updated(in.getUpdated())
                  .created(in.getCreated())
                  .tenantId(in.getTenantId())
                  .userId(in.getUserId())
                  .status(in.getStatus())
                  .progress(in.getProgress())
                  .minDisk(in.getMinDisk())
                  .minRam(in.getMinRam())
                  .server(in.getServer())
                  .metadata(in.getMetadata());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Date updated;
   private final Date created;
   @Named("tenant_id")
   private final String tenantId;
   @Named("user_id")
   private final String userId;
   private final Image.Status status;
   private final int progress;
   private final int minDisk;
   private final int minRam;
   private final Resource server;
   private final Map<String, String> metadata;

   @ConstructorProperties({
      "id", "name", "links", "updated", "created", "tenant_id", "user_id", "status", "progress", "minDisk", "minRam", "server", "metadata"
   })
   protected Image(String id, @Nullable String name, java.util.Set<Link> links, @Nullable Date updated, @Nullable Date created,
                   String tenantId, @Nullable String userId, @Nullable Status status, int progress, int minDisk, int minRam,
                   @Nullable Resource server, @Nullable Map<String, String> metadata) {
      super(id, name, links);
      this.updated = updated;
      this.created = created;
      this.tenantId = tenantId;
      this.userId = userId;
      this.status = status;
      this.progress = progress;
      this.minDisk = minDisk;
      this.minRam = minRam;
      this.server = server;
      this.metadata = metadata == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(metadata);
   }

   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   @Nullable
   public String getTenantId() {
      return this.tenantId;
   }

   @Nullable
   public String getUserId() {
      return this.userId;
   }

   @Nullable
   public Status getStatus() {
      return this.status;
   }

   public int getProgress() {
      return this.progress;
   }

   public int getMinDisk() {
      return this.minDisk;
   }

   public int getMinRam() {
      return this.minRam;
   }

   @Nullable
   public Resource getServer() {
      return this.server;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(updated, created, tenantId, userId, status, progress, minDisk, minRam, server, metadata);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Image that = Image.class.cast(obj);
      return super.equals(that) && Objects.equal(this.updated, that.updated)
               && Objects.equal(this.created, that.created)
               && Objects.equal(this.tenantId, that.tenantId)
               && Objects.equal(this.userId, that.userId)
               && Objects.equal(this.status, that.status)
               && Objects.equal(this.progress, that.progress)
               && Objects.equal(this.minDisk, that.minDisk)
               && Objects.equal(this.minRam, that.minRam)
               && Objects.equal(this.server, that.server)
               && Objects.equal(this.metadata, that.metadata);
   }
   
   protected ToStringHelper string() {
      return super.string()
            .add("updated", updated).add("created", created).add("tenantId", tenantId).add("userId", userId).add("status", status).add("progress", progress).add("minDisk", minDisk).add("minRam", minRam).add("server", server).add("metadata", metadata);
   }
   
}
