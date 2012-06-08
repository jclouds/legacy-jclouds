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

import java.util.Date;
import java.util.Map;

import org.jclouds.openstack.v2_0.domain.Resource;

import com.google.common.base.Objects;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

/**
 * An image is a collection of files you use to create or rebuild a server. Operators provide
 * pre-built OS images by default. You may also create custom images.
 * 
 * @author Jeremy Daggett
 * @see <a href= "http://docs.openstack.org/api/openstack-compute/1.1/content/Images-d1e4427.html"
 *      />
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
      private Date updated;
      private Date created;
      private String tenantId;
      private String userId;
      private Image.Status status;
      private int progress;
      private int minDisk;
      private int minRam;
      private Resource server;
      private Map<String, String> metadata = ImmutableMap.of();

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
         this.metadata = metadata;
         return self();
      }

      public Image build() {
         return new Image(this);
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
   
   protected Image() {
      // we want serializers like Gson to work w/o using sun.misc.Unsafe,
      // prohibited in GAE. This also implies fields are not final.
      // see http://code.google.com/p/jclouds/issues/detail?id=925
   }
  
   private Date updated;
   private Date created;
   @SerializedName("tenant_id")
   private String tenantId;
   @SerializedName("user_id")
   private String userId;
   private Status status;
   private int progress;
   private int minDisk;
   private int minRam;
   private Resource server;
   private Map<String, String> metadata = ImmutableMap.of();

   protected Image(Builder<?> builder) {
      super(builder);
      this.updated = builder.updated;
      this.created = builder.created;
      this.tenantId = builder.tenantId;
      this.userId = builder.userId;
      this.status = builder.status;
      this.progress = builder.progress;
      this.minDisk = builder.minDisk;
      this.minRam = builder.minRam;
      this.server = builder.server;
      this.metadata = ImmutableMap.copyOf(builder.metadata);
   }

   public Date getUpdated() {
      return this.updated;
   }

   public Date getCreated() {
      return this.created;
   }

   public String getTenantId() {
      return this.tenantId;
   }

   public String getUserId() {
      return this.userId;
   }

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

   public Resource getServer() {
      return this.server;
   }

   public Map<String, String> getMetadata() {
      // in case this was assigned in gson
      return ImmutableMap.copyOf(Maps.filterValues(this.metadata, Predicates.notNull()));
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("updated", updated)
            .add("created", created)
            .add("tenantId", tenantId)
            .add("userId", userId)
            .add("status", status)
            .add("progress", progress)
            .add("minDisk", minDisk)
            .add("minRam", minRam)
            .add("server", server)
            .add("metadata", metadata);
   }

}
