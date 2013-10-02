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
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableMap;

/**
 * A server is a virtual machine instance in the Cloud Servers system. Flavor and image are
 * requisite elements when creating a server.
 * 
 * @author Adrian Cole
*/
public class Server {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromServer(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected int id;
      protected String name;
      protected Map<String, String> metadata;
      protected Addresses addresses;
      protected String adminPass;
      protected Integer flavorId;
      protected String hostId;
      protected Integer imageId;
      protected Integer sharedIpGroupId;
      protected Integer progress;
      protected ServerStatus status;
   
      /** 
       * @see Server#getId()
       */
      public T id(int id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Server#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /** 
       * @see Server#getMetadata()
       */
      public T metadata(Map<String, String> metadata) {
         this.metadata = ImmutableMap.copyOf(checkNotNull(metadata, "metadata"));     
         return self();
      }

      /** 
       * @see Server#getAddresses()
       */
      public T addresses(Addresses addresses) {
         this.addresses = addresses;
         return self();
      }

      /** 
       * @see Server#getAdminPass()
       */
      public T adminPass(String adminPass) {
         this.adminPass = adminPass;
         return self();
      }

      /** 
       * @see Server#getFlavorId()
       */
      public T flavorId(Integer flavorId) {
         this.flavorId = flavorId;
         return self();
      }

      /** 
       * @see Server#getHostId()
       */
      public T hostId(String hostId) {
         this.hostId = hostId;
         return self();
      }

      /** 
       * @see Server#getImageId()
       */
      public T imageId(Integer imageId) {
         this.imageId = imageId;
         return self();
      }

      /** 
       * @see Server#getSharedIpGroupId()
       */
      public T sharedIpGroupId(Integer sharedIpGroupId) {
         this.sharedIpGroupId = sharedIpGroupId;
         return self();
      }

      /** 
       * @see Server#getProgress()
       */
      public T progress(Integer progress) {
         this.progress = progress;
         return self();
      }

      /** 
       * @see Server#getStatus()
       */
      public T status(ServerStatus status) {
         this.status = status;
         return self();
      }

      public Server build() {
         return new Server(id, name, metadata, addresses, adminPass, flavorId, hostId, imageId, sharedIpGroupId, progress, status);
      }
      
      public T fromServer(Server in) {
         return this
                  .id(in.getId())
                  .name(in.getName())
                  .metadata(in.getMetadata())
                  .addresses(in.getAddresses())
                  .adminPass(in.getAdminPass())
                  .flavorId(in.getFlavorId())
                  .hostId(in.getHostId())
                  .imageId(in.getImageId())
                  .sharedIpGroupId(in.getSharedIpGroupId())
                  .progress(in.getProgress())
                  .status(in.getStatus());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final int id;
   private final String name;
   private final Map<String, String> metadata;
   private final Addresses addresses;
   private final String adminPass;
   private final Integer flavorId;
   private final String hostId;
   private final Integer imageId;
   private final Integer sharedIpGroupId;
   private final Integer progress;
   private final ServerStatus status;

   @ConstructorProperties({
      "id", "name", "metadata", "addresses", "adminPass", "flavorId", "hostId", "imageId", "sharedIpGroupId", "progress", "status"
   })
   protected Server(int id, String name, @Nullable Map<String, String> metadata, @Nullable Addresses addresses,
                    @Nullable String adminPass, @Nullable Integer flavorId, @Nullable String hostId, @Nullable Integer imageId,
                    @Nullable Integer sharedIpGroupId, @Nullable Integer progress, @Nullable ServerStatus status) {
      this.id = id;
      this.name = checkNotNull(name, "name");
      this.metadata = metadata == null ? null : ImmutableMap.copyOf(metadata);     
      this.addresses = addresses;
      this.adminPass = adminPass;
      this.flavorId = flavorId;
      this.hostId = hostId;
      this.imageId = imageId;
      this.sharedIpGroupId = sharedIpGroupId;
      this.progress = progress;
      this.status = status == null ? ServerStatus.UNKNOWN : status;
   }

   public int getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   @Nullable
   public Map<String, String> getMetadata() {
      return this.metadata;
   }

   @Nullable
   public Addresses getAddresses() {
      return this.addresses;
   }

   @Nullable
   public String getAdminPass() {
      return this.adminPass;
   }

   @Nullable
   public Integer getFlavorId() {
      return this.flavorId;
   }

   /**
    * The Cloud Servers provisioning algorithm has an anti-affinity property that attempts to spread
    * out customer VMs across hosts. Under certain situations, VMs from the same customer may be
    * placed on the same host. hostId represents the host your cloud server runs on and can be used
    * to determine this scenario if it's relevant to your application.
    * <p/>
    * Note: hostId is unique PER ACCOUNT and is not globally unique.
    */
   @Nullable
   public String getHostId() {
      return this.hostId;
   }

   @Nullable
   public Integer getImageId() {
      return this.imageId;
   }

   @Nullable
   public Integer getSharedIpGroupId() {
      return this.sharedIpGroupId;
   }

   @Nullable
   public Integer getProgress() {
      return this.progress;
   }

   /**
    * Servers contain a status attribute that can be used as an indication of the current server
    * state. Servers with an ACTIVE status are available for use.
    */
   public ServerStatus getStatus() {
      return this.status;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name, metadata, addresses, adminPass, flavorId, hostId, imageId, sharedIpGroupId);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Server that = Server.class.cast(obj);
      return Objects.equal(this.id, that.id)
               && Objects.equal(this.name, that.name)
               && Objects.equal(this.metadata, that.metadata)
               && Objects.equal(this.addresses, that.addresses)
               && Objects.equal(this.adminPass, that.adminPass)
               && Objects.equal(this.flavorId, that.flavorId)
               && Objects.equal(this.hostId, that.hostId)
               && Objects.equal(this.imageId, that.imageId)
               && Objects.equal(this.sharedIpGroupId, that.sharedIpGroupId);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("id", id).add("name", name).add("metadata", metadata).add("addresses", addresses).add("adminPass", adminPass).add("flavorId", flavorId).add("hostId", hostId).add("imageId", imageId).add("sharedIpGroupId", sharedIpGroupId).add("progress", progress).add("status", status);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
