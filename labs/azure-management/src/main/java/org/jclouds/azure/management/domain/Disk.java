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
package org.jclouds.azure.management.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;

/**
 * disk in the image repository
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 * @author Adrian Cole
 */
public class Disk {
   public static class Attachment {

      public static Builder builder() {
         return new Builder();
      }

      public Builder toBuilder() {
         return builder().fromAttachment(this);
      }

      public static class Builder {

         private String hostedService;
         private String deployment;
         private String role;

         /**
          * @see Attachment#getHostedService()
          */
         public Builder hostedService(String hostedService) {
            this.hostedService = hostedService;
            return this;
         }

         /**
          * @see Attachment#getDeployment()
          */
         public Builder deployment(String deployment) {
            this.deployment = deployment;
            return this;
         }

         /**
          * @see Attachment#getRole()
          */
         public Builder role(String role) {
            this.role = role;
            return this;
         }

         public Attachment build() {
            return new Attachment(hostedService, deployment, role);
         }

         public Builder fromAttachment(Attachment in) {
            return this.hostedService(in.hostedService).deployment(in.deployment).role(in.role);
         }
      }

      private final String hostedService;
      private final String deployment;
      private final String role;

      protected Attachment(String hostedService, String deployment, String role) {
         this.hostedService = checkNotNull(hostedService, "hostedService");
         this.deployment = checkNotNull(deployment, "deployment");
         this.role = checkNotNull(role, "role");
      }

      /**
       * The deployment in which the disk is being used.
       */
      public String getDeployment() {
         return deployment;
      }

      /**
       * The hosted service in which the disk is being used.
       */
      public String getHostedService() {
         return hostedService;
      }

      /**
       * The virtual machine that the disk is attached to.
       */
      public String getRole() {
         return role;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public int hashCode() {
         return Objects.hashCode(hostedService, deployment, role);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Attachment other = (Attachment) obj;
         return Objects.equal(this.hostedService, other.hostedService)
                  && Objects.equal(this.deployment, other.deployment) && Objects.equal(this.role, other.role);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String toString() {
         return Objects.toStringHelper(this).omitNullValues().add("deployment", hostedService).add("role", role)
                  .toString();
      }

   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHostedService(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected Optional<Attachment> attachedTo = Optional.absent();
      protected OSType os;
      protected String name;
      protected Optional<Integer> logicalSizeInGB = Optional.absent();
      protected Optional<String> description = Optional.absent();
      protected Optional<String> location = Optional.absent();
      protected Optional<String> affinityGroup = Optional.absent();
      protected Optional<URI> mediaLink = Optional.absent();
      protected Optional<String> sourceImage = Optional.absent();
      protected Optional<String> label = Optional.absent();
      protected boolean hasOperatingSystem;
      protected boolean isCorrupted;

      /**
       * @see Disk#getAttachedTo()
       */
      public T attachedTo(Attachment attachedTo) {
         this.attachedTo = Optional.fromNullable(attachedTo);
         return self();
      }

      /**
       * @see Disk#getOS()
       */
      public T os(OSType os) {
         this.os = os;
         return self();
      }

      /**
       * @see Disk#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see Disk#getDescription()
       */
      public T description(String description) {
         this.description = Optional.fromNullable(description);
         return self();
      }

      /**
       * @see Disk#getLogicalSizeInGB()
       */
      public T logicalSizeInGB(Integer logicalSizeInGB) {
         this.logicalSizeInGB = Optional.fromNullable(logicalSizeInGB);
         return self();
      }

      /**
       * @see Disk#getLocation()
       */
      public T location(String location) {
         this.location = Optional.fromNullable(location);
         return self();
      }

      /**
       * @see Disk#getAffinityGroup()
       */
      public T affinityGroup(String affinityGroup) {
         this.affinityGroup = Optional.fromNullable(affinityGroup);
         return self();
      }

      /**
       * @see Disk#getMediaLink()
       */
      public T mediaLink(URI mediaLink) {
         this.mediaLink = Optional.fromNullable(mediaLink);
         return self();
      }

      /**
       * @see Disk#getSourceImage()
       */
      public T sourceImage(String sourceImage) {
         this.sourceImage = Optional.fromNullable(sourceImage);
         return self();
      }

      /**
       * @see Disk#getLabel()
       */
      public T label(String label) {
         this.label = Optional.fromNullable(label);
         return self();
      }

      /**
       * @see Disk#hasOperatingSystem()
       */
      public T hasOperatingSystem(boolean hasOperatingSystem) {
         this.hasOperatingSystem = hasOperatingSystem;
         return self();
      }

      /**
       * @see Disk#isCorrupted()
       */
      public T isCorrupted(boolean isCorrupted) {
         this.isCorrupted = isCorrupted;
         return self();
      }

      public Disk build() {
         return new Disk(attachedTo, os, name, logicalSizeInGB, description, location, affinityGroup, mediaLink,
                  sourceImage, label, hasOperatingSystem, isCorrupted);
      }

      public T fromHostedService(Disk in) {
         return this.attachedTo(in.attachedTo.orNull()).os(in.getOS()).name(in.getName())
                  .logicalSizeInGB(in.getLogicalSizeInGB().orNull()).description(in.getDescription().orNull())
                  .location(in.getLocation().orNull()).affinityGroup(in.getAffinityGroup().orNull())
                  .mediaLink(in.getMediaLink().orNull()).sourceImage(in.getSourceImage().orNull())
                  .label(in.getLabel().orNull()).hasOperatingSystem(in.hasOperatingSystem).isCorrupted(in.isCorrupted);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final Optional<Attachment> attachedTo;
   protected final OSType os;
   protected final String name;
   protected final Optional<Integer> logicalSizeInGB;
   protected final Optional<String> description;
   protected final Optional<String> location;
   protected final Optional<String> affinityGroup;
   protected final Optional<URI> mediaLink;
   protected final Optional<String> sourceImage;
   protected final Optional<String> label;
   protected final boolean hasOperatingSystem;
   protected final boolean isCorrupted;

   protected Disk(Optional<Attachment> attachedTo, OSType os, String name, Optional<Integer> logicalSizeInGB,
            Optional<String> description, Optional<String> location, Optional<String> affinityGroup,
            Optional<URI> mediaLink, Optional<String> sourceImage, Optional<String> label, boolean hasOperatingSystem,
            boolean isCorrupted) {
      this.name = checkNotNull(name, "name");
      this.attachedTo = checkNotNull(attachedTo, "attachedTo for %s", name);
      this.logicalSizeInGB = checkNotNull(logicalSizeInGB, "logicalSizeInGB for %s", name);
      this.description = checkNotNull(description, "description for %s", name);
      this.os = checkNotNull(os, "os for %s", name);
      this.location = checkNotNull(location, "location for %s", name);
      this.affinityGroup = checkNotNull(affinityGroup, "affinityGroup for %s", name);
      this.mediaLink = checkNotNull(mediaLink, "mediaLink for %s", name);
      this.sourceImage = checkNotNull(sourceImage, "sourceImage for %s", name);
      this.label = checkNotNull(label, "label for %s", name);
      this.hasOperatingSystem = hasOperatingSystem;
      this.isCorrupted = isCorrupted;
   }

   /**
    * Contains properties that specify a virtual machine that currently using the disk. A disk
    * cannot be deleted as long as it is attached to a virtual machine.
    */
   public Optional<Attachment> getAttachedTo() {
      return attachedTo;
   }

   /**
    * The operating system type of the OS image.
    */
   public OSType getOS() {
      return os;
   }

   /**
    * The name of the disk. This is the name that is used when creating one or more virtual machines
    * using the disk.
    */
   public String getName() {
      return name;
   }

   /**
    * The size, in GB, of the image.
    */
   public Optional<Integer> getLogicalSizeInGB() {
      return logicalSizeInGB;
   }

   /**
    * The description for the image.
    */
   public Optional<String> getDescription() {
      return description;
   }

   /**
    * The geo-location in which this media is located. The Location value is derived from storage
    * account that contains the blob in which the media is located. If the storage account belongs
    * to an affinity group the value is absent.
    */
   public Optional<String> getLocation() {
      return location;
   }

   /**
    * The affinity in which the media is located. The AffinityGroup value is derived from storage
    * account that contains the blob in which the media is located. If the storage account does not
    * belong to an affinity group the value is absent.
    */
   public Optional<String> getAffinityGroup() {
      return affinityGroup;
   }

   /**
    * The location of the blob in the blob store in which the media for the disk is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    * 
    * Example:
    * 
    * http://example.blob.core.windows.net/disks/mydisk.vhd
    */
   public Optional<URI> getMediaLink() {
      return mediaLink;
   }

   /**
    * The name of the OS Image from which the disk was created. This property is populated
    * automatically when a disk is created from an OS image by calling the Add Role, Create
    * Deployment, or Provision Disk operations.
    */
   public Optional<String> getSourceImage() {
      return sourceImage;
   }

   /**
    * The description of the image.
    */
   public Optional<String> getLabel() {
      return label;
   }

   /**
    * Returns whether this disk contains operation system. Only disks that have an operating system
    * installed can be mounted as an OS Drive.
    */
   public boolean hasOperatingSystem() {
      return hasOperatingSystem;
   }

   /**
    * Returns whether there is a consistency failure detected with this disk. If a disk fails the
    * consistency check, you delete any virtual machines using it, delete the disk, and inspect the
    * blob media to see if the content is intact. You can then reregister the media in the blob as a
    * disk.
    */
   public boolean isCorrupted() {
      return isCorrupted;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      Disk other = (Disk) obj;
      return Objects.equal(this.name, other.name);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("os", os).add("name", name)
               .add("attachedTo", attachedTo.orNull()).add("logicalSizeInGB", logicalSizeInGB.orNull())
               .add("description", description).add("location", location.orNull())
               .add("affinityGroup", affinityGroup.orNull()).add("mediaLink", mediaLink.orNull())
               .add("sourceImage", sourceImage.orNull()).add("label", label.orNull())
               .add("hasOperatingSystem", hasOperatingSystem).add("isCorrupted", isCorrupted);
   }

}
