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
package org.jclouds.openstack.nova.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * An image is a collection of files used to create or rebuild a server. Rackspace provides a number
 * of pre-built OS images by default. You may also create custom images from cloud servers you have
 * launched. These custom images are useful for backup purposes or for producing gold server images
 * if you plan to deploy a particular server configuration frequently.
 *
 * @author Adrian Cole
 */
public class Image extends Resource {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromImage(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {
      protected String name;
      protected Integer progress;
      protected String serverRef;
      protected ImageStatus status;
      protected Map<String, String> metadata = ImmutableMap.of();
      protected Date created;
      protected Date updated;

      /**
       * @see Image#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Image#getProgress()
       */
      public T progress(Integer progress) {
         this.progress = progress;
         return self();
      }

      /**
       * @see Image#getServerRef()
       */
      public T serverRef(String serverRef) {
         this.serverRef = serverRef;
         return self();
      }

      /**
       * @see Image#getStatus()
       */
      public T status(ImageStatus status) {
         this.status = status;
         return self();
      }

      /**
       * @see Image#getMetadata()
       */
      public T metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
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
       * @see Image#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      public Image build() {
         return new Image(id, links, orderedSelfReferences, name, progress, serverRef, status, metadata, created, updated);
      }

      public T fromImage(Image in) {
         return super.fromResource(in)
               .id(in.getId())
               .name(in.getName())
               .progress(in.getProgress())
               .serverRef(in.getServerRef())
               .status(in.getStatus())
               .metadata(in.getMetadata())
               .created(in.getCreated())
               .updated(in.getUpdated());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String name;
   private final Integer progress;
   private final String serverRef;
   private final ImageStatus status;
   private final Map<String, String> metadata;
   private final Date created;
   private final Date updated;

   @ConstructorProperties({
        "id", "links", "orderedSelfReferences", "name", "progress", "serverRef", "status", "metadata", "created", "updated"
   })
   protected Image(int id, List<Map<String, String>> links, @Nullable Map<LinkType, URI> orderedSelfReferences, @Nullable String name,
                   @Nullable Integer progress, @Nullable String serverRef, @Nullable ImageStatus status, @Nullable Map<String, String> metadata,
                   @Nullable Date created, @Nullable Date updated) {
      super(id, links, orderedSelfReferences);
      this.name = name;
      this.progress = progress;
      this.serverRef = serverRef;
      this.status = status == null ? ImageStatus.UNKNOWN : status;
      this.metadata = metadata == null ? ImmutableMap.<String, String>of() : ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));
      this.created = created;
      this.updated = updated;
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   @Nullable
   public Integer getProgress() {
      return this.progress;
   }

   @Nullable
   public String getServerRef() {
      return this.serverRef;
   }

   @Nullable
   public ImageStatus getStatus() {
      return this.status;
   }

   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), name, serverRef);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Image that = Image.class.cast(obj);
      return super.equals(that)
            && Objects.equal(this.name, that.name)
            && Objects.equal(this.serverRef, that.serverRef);
   }

   protected ToStringHelper string() {
      return super.string().add("name", name).add("progress", progress).add("serverRef", serverRef).add("status", status)
            .add("metadata", metadata).add("created", created).add("updated", updated);
   }

}
