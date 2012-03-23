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

import static com.google.common.base.Objects.toStringHelper;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.domain.Link;
import org.jclouds.openstack.domain.Resource;

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

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImage(this);
   }

   public static class Builder extends Resource.Builder {

      private Date updated;
      private Date created;
      private String tenantId;
      private String userId;
      private Status status;
      private int progress;
      private int minDisk;
      private int minRam;
      private Resource server;
      private Map<String, String> metadata = Maps.newLinkedHashMap();

      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder tenantId(String tenantId) {
         this.tenantId = tenantId;
         return this;
      }

      public Builder userId(String userId) {
         this.userId = userId;
         return this;
      }

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder progress(int progress) {
         this.progress = progress;
         return this;
      }

      public Builder minDisk(int minDisk) {
         this.minDisk = minDisk;
         return this;
      }

      public Builder minRam(int minRam) {
         this.minRam = minRam;
         return this;
      }

      public Builder server(Resource server) {
         this.server = server;
         return this;
      }

      public Builder metadata(Map<String, String> metadata) {
         this.metadata = metadata;
         return this;
      }

      public Image build() {
         return new Image(id, name, links, updated, created, tenantId, userId, status, progress, minDisk, minRam,
                  server, metadata);
      }

      public Builder fromImage(Image in) {
         return fromResource(in).status(in.getStatus()).updated(in.getUpdated()).created(in.getCreated()).progress(
                  in.getProgress()).server(in.getServer()).metadata(in.getMetadata());
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

   private final Date updated;
   private final Date created;
   @SerializedName("tenant_id")
   private final String tenantId;
   @SerializedName("user_id")
   private final String userId;
   private final Status status;
   private final int progress;
   private final int minDisk;
   private final int minRam;
   private final Resource server;
   private final Map<String, String> metadata;

   protected Image(String id, String name, Set<Link> links, Date updated, Date created, String tenantId, String userId,
            Status status, int progress, int minDisk, int minRam, Resource server, Map<String, String> metadata) {
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
      this.metadata = ImmutableMap.<String, String> copyOf(metadata);
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
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name).add("links", links).add("updated", updated).add(
               "created", created).add("tenantId", tenantId).add("userId", userId).add("status", status).add(
               "progress", progress).add("minDisk", minDisk).add("minRam", minRam).add("server", server).add(
               "metadata", metadata).toString();
   }

}
