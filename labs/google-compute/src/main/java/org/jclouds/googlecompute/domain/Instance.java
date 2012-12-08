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

package org.jclouds.googlecompute.domain;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.jclouds.javax.annotation.Nullable;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.Set;

/**
 * Represents a virtual machine.
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/instances"/>
 */
public class Instance extends Resource {

   public enum Status {
      PROVISIONING,
      STAGING,
      RUNNING,
      STOPPING,
      STOPPED,
      TERMINATED
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromInstance(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T> {

      private ImmutableSet.Builder<String> tags = ImmutableSet.builder();
      private String image;
      private String machineType;
      private Status status;
      private String statusMessage;
      private String zone;
      private ImmutableSet.Builder<InstanceNetworkInterface> networkInterfaces = ImmutableSet.builder();
      private ImmutableSet.Builder<InstanceAttachedDisk> disks = ImmutableSet.builder();
      private Metadata metadata;
      private ImmutableSet.Builder<InstanceServiceAccount> serviceAccounts = ImmutableSet.builder();

      /**
       * @see Instance#getTags()
       */
      public T addTag(String tag) {
         this.tags.add(tag);
         return self();
      }

      /**
       * @see Instance#getTags()
       */
      public T tags(Set<String> tags) {
         this.tags.addAll(tags);
         return self();
      }

      /**
       * @see Instance#getImage()
       */
      public T image(String image) {
         this.image = image;
         return self();
      }

      /**
       * @see Instance#getMachineType()
       */
      public T machineType(String machineType) {
         this.machineType = machineType;
         return self();
      }

      /**
       * @see Instance#getStatus()
       */
      public T status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see Instance#getStatusMessage()
       */
      public T statusMessage(String statusMessage) {
         this.statusMessage = statusMessage;
         return self();
      }

      /**
       * @see Instance#getZone()
       */
      public T zone(String zone) {
         this.zone = zone;
         return self();
      }

      /**
       * @see Instance#getNetworkInterfaces()
       */
      public T addNetworkInterface(InstanceNetworkInterface networkInterface) {
         this.networkInterfaces.add(networkInterface);
         return self();
      }

      /**
       * @see Instance#getNetworkInterfaces()
       */
      public T networkInterfaces(Set<InstanceNetworkInterface> networkInterfaces) {
         this.networkInterfaces.addAll(networkInterfaces);
         return self();
      }

      /**
       * @see Instance#getDisks()
       */
      public T addDisk(InstanceAttachedDisk disk) {
         this.disks.add(disk);
         return self();
      }

      /**
       * @see Instance#getDisks()
       */
      public T disks(Set<InstanceAttachedDisk> disks) {
         this.disks.addAll(disks);
         return self();
      }

      /**
       * @see Instance#getMetadata()
       */
      public T metadata(Metadata metadata) {
         this.metadata = metadata;
         return self();
      }

      /**
       * @see Instance#getServiceAccounts()
       */
      public T addServiceAccount(InstanceServiceAccount serviceAccount) {
         this.serviceAccounts.add(serviceAccount);
         return self();
      }

      /**
       * @see Instance#getServiceAccounts()
       */
      public T serviceAccoutns(Set<InstanceServiceAccount> serviceAccounts) {
         this.serviceAccounts.addAll(serviceAccounts);
         return self();
      }


      public Instance build() {
         return new Instance(super.id, super.creationTimestamp, super.selfLink, super.name,
                 super.description, tags.build(), image, machineType, status, statusMessage, zone,
                 networkInterfaces.build(), disks.build(), metadata, serviceAccounts.build());
      }

      public T fromInstance(Instance in) {
         return super.fromResource(in).tags(in.getTags()).image(in.getImage()).machineType(in.getMachineType())
                 .status(in.getStatus()).statusMessage(in.getStatusMessage()).zone(in.getZone()).networkInterfaces(in
                         .getNetworkInterfaces()).disks(in.getDisks()).metadata(in.getMetadata())
                 .serviceAccoutns(in
                         .getServiceAccounts());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Set<String> tags;
   protected final String image;
   protected final String machineType;
   protected final Status status;
   protected final String statusMessage;
   protected final String zone;
   protected final Set<InstanceNetworkInterface> networkInterfaces;
   protected final Set<InstanceAttachedDisk> disks;
   protected final Metadata metadata;
   protected final Set<InstanceServiceAccount> serviceAccounts;

   @ConstructorProperties({
           "id", "creationTimestamp", "selfLink", "name", "description", "tags", "image",
           "machineType", "status", "statusMessage", "zone", "networkInterfaces", "disks", "metadata",
           "serviceAccounts"
   })
   protected Instance(String id, Date creationTimestamp, String selfLink, String name, String description,
                      Set<String> tags, String image, String machineType, Status status, String statusMessage,
                      String zone, Set<InstanceNetworkInterface> networkInterfaces,
                      Set<InstanceAttachedDisk> disks,
                      Metadata metadata, Set<InstanceServiceAccount> serviceAccounts) {
      super(Kind.INSTANCE, id, creationTimestamp, selfLink, name, description);
      this.tags = nullCollectionOnNullOrEmpty(tags);
      this.image = image;
      this.machineType = machineType;
      this.status = status;
      this.statusMessage = statusMessage;
      this.zone = zone;
      this.networkInterfaces = nullCollectionOnNullOrEmpty(networkInterfaces);
      this.disks = nullCollectionOnNullOrEmpty(disks);
      this.metadata = metadata;
      this.serviceAccounts = nullCollectionOnNullOrEmpty(serviceAccounts);
   }

   /**
    * @return an optional set of tags applied to this instance. Used to identify valid sources or targets for network
    *         firewalls. Provided by the client when the instance is created. Each tag must be unique,
    *         must be 1-63 characters long, and comply with RFC1035.
    */
   @Nullable
   public Set<String> getTags() {
      return tags;
   }

   /**
    * @return an optional URL of the disk image resource to be to be installed on this instance; provided by the
    *         client when the instance is created. If not specified, the server will choose a default image.
    */
   @Nullable
   public String getImage() {
      return image;
   }

   /**
    * @return URL of the machine type resource describing which machine type to use to host the instance; provided by
    *         the client when the instance is created.
    */
   public String getMachineType() {
      return machineType;
   }

   /**
    * @return Instance status
    * @see Instance.Status
    */
   public Status getStatus() {
      return status;
   }

   /**
    * @return an optional, human-readable explanation of the status (output only).
    */
   @Nullable
   public String getStatusMessage() {
      return statusMessage;
   }

   /**
    * @return URL of the zone resource describing where this instance should be hosted; provided by the client when
    *         the instance is created.
    */
   public String getZone() {
      return zone;
   }

   /**
    * @return set of NetworkInterfaces
    * @see InstanceNetworkInterface
    */
   public Set<InstanceNetworkInterface> getNetworkInterfaces() {
      return networkInterfaces;
   }

   /**
    * @return array of disks associated with this instance. Persistent disks must be created before
    *         you can assign
    *         them.
    * @see Disk
    */
   public Set<InstanceAttachedDisk> getDisks() {
      return disks;
   }

   /**
    * @return metadata for this instance
    * @see Metadata
    */
   public Metadata getMetadata() {
      return metadata;
   }

   /**
    * @return list of service accounts each with specified scopes, for which access tokens are to be made available
    *         to the instance through metadata queries.
    * @see InstanceServiceAccount
    */
   public Set<InstanceServiceAccount> getServiceAccounts() {
      return serviceAccounts;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(kind, id, creationTimestamp, selfLink, name, description, tags, image,
              machineType, status, statusMessage, zone, networkInterfaces, disks, metadata, serviceAccounts);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Instance that = Instance.class.cast(obj);
      return super.equals(that)
              && Objects.equal(this.tags, that.tags)
              && Objects.equal(this.image, that.image)
              && Objects.equal(this.machineType, that.machineType)
              && Objects.equal(this.status, that.status)
              && Objects.equal(this.statusMessage, that.statusMessage)
              && Objects.equal(this.zone, that.zone)
              && Objects.equal(this.networkInterfaces, that.networkInterfaces)
              && Objects.equal(this.disks, that.disks)
              && Objects.equal(this.metadata, that.metadata)
              && Objects.equal(this.serviceAccounts, that.serviceAccounts);
   }

   /**
    * {@inheritDoc}
    */
   protected Objects.ToStringHelper string() {
      return super.string()
              .add("tags", tags)
              .add("image", image)
              .add("machineType", machineType)
              .add("status", status)
              .add("statusMessage", statusMessage)
              .add("zone", zone)
              .add("networkInterfaces", networkInterfaces)
              .add("disks", disks)
              .add("metadata", metadata)
              .add("serviceAccounts", serviceAccounts);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }
}
