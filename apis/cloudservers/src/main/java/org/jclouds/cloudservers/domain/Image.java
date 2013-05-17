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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An image is a collection of files used to create or rebuild a server. Rackspace provides a number
 * of pre-built OS images by default. You may also create custom images from cloud servers you have
 * launched. These custom images are useful for backup purposes or for producing gold server images
 * if you plan to deploy a particular server configuration frequently.
 * 
 * @author Adrian Cole
*/
public class Image {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromImage(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected Date created;
      protected int id;
      protected String name;
      protected Integer progress;
      protected Integer serverId;
      protected ImageStatus status;
      protected Date updated;
   
      /** 
       * @see Image#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /** 
       * @see Image#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

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
       * @see Image#getServerId()
       */
      public T serverId(Integer serverId) {
         this.serverId = serverId;
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
       * @see Image#getUpdated()
       */
      public T updated(Date updated) {
         this.updated = updated;
         return self();
      }

      public Image build() {
         return new Image(created, id, name, progress, serverId, status, updated);
      }
      
      public T fromImage(Image in) {
         return this
                  .created(in.getCreated())
                  .id(in.getId())
                  .name(in.getName())
                  .progress(in.getProgress())
                  .serverId(in.getServerId())
                  .status(in.getStatus())
                  .updated(in.getUpdated());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final Date created;
   private final int id;
   private final String name;
   private final Integer progress;
   private final Integer serverId;
   private final ImageStatus status;
   private final Date updated;

   @ConstructorProperties({
      "created", "id", "name", "progress", "serverId", "status", "updated"
   })
   protected Image(@Nullable Date created, int id, String name, @Nullable Integer progress, @Nullable Integer serverId, @Nullable ImageStatus status, @Nullable Date updated) {
      this.created = created;
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.progress = progress;
      this.serverId = serverId;
      this.status = status;
      this.updated = updated;
   }

   @Nullable
   public Date getCreated() {
      return this.created;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public Integer getProgress() {
      return this.progress;
   }

   @Nullable
   public Integer getServerId() {
      return this.serverId;
   }

   @Nullable
   public ImageStatus getStatus() {
      return this.status;
   }

   @Nullable
   public Date getUpdated() {
      return this.updated;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(created, id, name, progress, serverId, status, updated);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Image that = Image.class.cast(obj);
      return Objects.equal(this.created, that.created)
               && Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.progress, that.progress)
               && Objects.equal(this.serverId, that.serverId)
               && Objects.equal(this.status, that.status)
               && Objects.equal(this.updated, that.updated);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("created", created).add("id", id).add("name", name).add("progress", progress).add("serverId", serverId).add("status", status).add("updated", updated);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
