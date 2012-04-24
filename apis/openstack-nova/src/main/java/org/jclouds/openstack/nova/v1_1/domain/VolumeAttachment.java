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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An Openstack Nova Volume Attachment (describes how Volumes are attached to Servers)
 */
public class VolumeAttachment {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromAttachment(this);
   }

   public static abstract class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private String id;
      private String volumeId;
      private String serverId;
      private String device;

      public T id(String id) {
         this.id = id;
         return self();
      }

      public T volumeId(String volumeId) {
         this.volumeId = volumeId;
         return self();
      }

      public T serverId(String serverId) {
         this.serverId = serverId;
         return self();
      }

      public T device(String device) {
         this.device = device;
         return self();
      }

      public VolumeAttachment build() {
         return new VolumeAttachment(this);
      }

      public T fromAttachment(VolumeAttachment in) {
         return this
               .id(in.getId())
               .volumeId(in.getVolumeId())
               .serverId(in.getServerId())
               .device(in.getDevice())
               ;
      }

   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String id;
   private final String volumeId;
   private final String serverId;
   private final String device;

   protected VolumeAttachment(Builder<?> builder) {
      this.id = checkNotNull(builder.id, "id");
      this.volumeId = checkNotNull(builder.volumeId, "volumeId");
      this.serverId = builder.serverId;
      this.device = builder.device;
   }

   /**
    * @return the attachment id (typically the same as #getVolumeId())
    */
   public String getId() {
      return this.id;
   }

   /**
    * @return the id of the volume attached
    */
   public String getVolumeId() {
      return this.volumeId;
   }

   /**
    * @return the id of the server the volume is attached to
    */
   @Nullable
   public String getServerId() {
      return this.serverId;
   }

   /**
    * @return the device name (e.g. "/dev/vdc")
    */
   @Nullable
   public String getDevice() {
      return this.device;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, volumeId, serverId, device);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      VolumeAttachment that = VolumeAttachment.class.cast(obj);
      return Objects.equal(this.id, that.id)
            && Objects.equal(this.volumeId, that.volumeId)
            && Objects.equal(this.serverId, that.serverId)
            && Objects.equal(this.device, that.device)
            ;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
            .add("id", id)
            .add("volumeId", volumeId)
            .add("serverId", serverId)
            .add("device", device)
            ;
   }

   @Override
   public String toString() {
      return string().toString();
   }

}