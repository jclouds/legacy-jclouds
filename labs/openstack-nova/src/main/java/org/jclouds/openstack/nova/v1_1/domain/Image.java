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
import org.jclouds.openstack.nova.v1_1.domain.ImageStatus;

import com.google.common.collect.Maps;

/**
 * An image is a collection of files you use to create or rebuild a server.
 * Operators provide pre-built OS images by default. You may also create custom
 * images.
 * 
 * @author Jeremy Daggett
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Images-d1e4427.html"
 *      />
 */
public class Image extends Resource {
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromImage(this);
   }

   public static class Builder extends Resource.Builder {

      private ImageStatus status;
      private Date updated;
      private Date created;
      private int progress;
      private String serverRef;
      private Map<String, String> metadata = Maps.newHashMap();

      public Builder status(ImageStatus status) {
         this.status = status;
         return this;
      }

      public Builder updated(Date updated) {
         this.updated = updated;
         return this;
      }

      public Builder created(Date created) {
         this.created = created;
         return this;
      }

      public Builder progress(int progress) {
         this.progress = progress;
         return this;
      }

      public Builder serverRef(String serverRef) {
         this.serverRef = serverRef;
         return this;
      }

      public Builder metadata(Map<String, String> metadata) {
         this.metadata = metadata;
         return this;
      }

      public Image build() {
         return new Image(id, name, links, status, updated, created, progress,
               serverRef, metadata);
      }

      public Builder fromImage(Image in) {
         return fromResource(in).status(in.getStatus())
               .updated(in.getUpdated()).created(in.getCreated())
               .progress(in.getProgress()).serverRef(in.getServerRef())
               .metadata(in.getMetadata());
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

   private ImageStatus status;
   private Date updated;
   private Date created;
   private int progress;
   private String serverRef;
   private Map<String, String> metadata = Maps.newHashMap();
   
   protected Image(String id, String name, Set<Link> links, ImageStatus status,
         Date updated, Date created, int progress, String serverRef,
         Map<String, String> metadata) {
      super(id, name, links);
      this.status = status;
      this.updated = updated;
      this.created = created;
      this.progress = progress;
      this.serverRef = serverRef;
      this.metadata = metadata;
   }

   public ImageStatus getStatus() {
      return this.status;
   }

   public Date getUpdated() {
      return this.updated;
   }

   public Date getCreated() {
      return this.created;
   }
   
   public int getProgress() {
      return this.progress;
   }
   
   public String getServerRef() {
      return this.serverRef;
   }
   
   public Map<String, String> getMetadata() {
      return this.metadata;
   }
   
   
   @Override
   public String toString() {
      return toStringHelper("").add("id", id).add("name", name)
            .add("links", links).add("status", status).add("updated", updated)
            .add("created", created).add("progress", progress).add("serverRef", serverRef)
            .add("metadata", metadata).toString();
   }

}
