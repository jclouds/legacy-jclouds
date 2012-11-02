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
 * OS images from the image repository
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157191" >api</a>
 * @author Adrian Cole
 */
public class OSImage {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHostedService(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected OSType os;
      protected String name;
      protected Optional<Integer> logicalSizeInGB = Optional.absent();
      protected Optional<String> description = Optional.absent();
      protected Optional<String> category = Optional.absent();
      protected Optional<String> location = Optional.absent();
      protected Optional<String> affinityGroup = Optional.absent();
      protected Optional<URI> mediaLink = Optional.absent();
      protected Optional<URI> eula = Optional.absent();
      protected String label;

      /**
       * @see OSImage#getOS()
       */
      public T os(OSType os) {
         this.os = os;
         return self();
      }

      /**
       * @see OSImage#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see OSImage#getDescription()
       */
      public T description(String description) {
         this.description = Optional.fromNullable(description);
         return self();
      }

      /**
       * @see OSImage#getLogicalSizeInGB()
       */
      public T logicalSizeInGB(Integer logicalSizeInGB) {
         this.logicalSizeInGB = Optional.fromNullable(logicalSizeInGB);
         return self();
      }

      /**
       * @see OSImage#getCategory()
       */
      public T category(String category) {
         this.category = Optional.fromNullable(category);
         return self();
      }

      /**
       * @see OSImage#getLocation()
       */
      public T location(String location) {
         this.location = Optional.fromNullable(location);
         return self();
      }

      /**
       * @see OSImage#getAffinityGroup()
       */
      public T affinityGroup(String affinityGroup) {
         this.affinityGroup = Optional.fromNullable(affinityGroup);
         return self();
      }

      /**
       * @see OSImage#getMediaLink()
       */
      public T mediaLink(URI mediaLink) {
         this.mediaLink = Optional.fromNullable(mediaLink);
         return self();
      }

      /**
       * @see OSImage#getEula()
       */
      public T eula(URI eula) {
         this.eula = Optional.fromNullable(eula);
         return self();
      }

      /**
       * @see OSImage#getLabel()
       */
      public T label(String label) {
         this.label = label;
         return self();
      }

      public OSImage build() {
         return new OSImage(os, name, logicalSizeInGB, description, category, location, affinityGroup, mediaLink, eula,
                  label);
      }

      public T fromHostedService(OSImage in) {
         return this.os(in.getOS()).name(in.getName()).logicalSizeInGB(in.getLogicalSizeInGB().orNull())
                  .description(in.getDescription().orNull()).category(in.getCategory().orNull())
                  .location(in.getLocation().orNull()).affinityGroup(in.getAffinityGroup().orNull())
                  .mediaLink(in.getMediaLink().orNull()).eula(in.getEula().orNull()).label(in.getLabel());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final OSType os;
   protected final String name;
   protected final Optional<Integer> logicalSizeInGB;
   protected final Optional<String> description;
   protected final Optional<String> category;
   protected final Optional<String> location;
   protected final Optional<String> affinityGroup;
   protected final Optional<URI> mediaLink;
   protected final Optional<URI> eula;
   protected final String label;

   protected OSImage(OSType os, String name, Optional<Integer> logicalSizeInGB, Optional<String> description,
            Optional<String> category, Optional<String> location, Optional<String> affinityGroup,
            Optional<URI> mediaLink, Optional<URI> eula, String label) {
      this.name = checkNotNull(name, "name");
      this.logicalSizeInGB = checkNotNull(logicalSizeInGB, "logicalSizeInGB for %s", name);
      this.description = checkNotNull(description, "description for %s", name);
      this.os = checkNotNull(os, "os for %s", name);
      this.category = checkNotNull(category, "category for %s", name);
      this.location = checkNotNull(location, "location for %s", name);
      this.affinityGroup = checkNotNull(affinityGroup, "affinityGroup for %s", name);
      this.mediaLink = checkNotNull(mediaLink, "mediaLink for %s", name);
      this.eula = checkNotNull(eula, "eula for %s", name);
      this.label = checkNotNull(label, "label for %s", name);
   }

   /**
    * The operating system type of the OS image.
    */
   public OSType getOS() {
      return os;
   }

   /**
    * The name of the hosted service. This name is the DNS prefix name and can be used to access the
    * hosted service.
    * 
    * For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
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
    * The repository classification of image. All user images have the category "User", but
    * categories for other images could be, for example "Canonical"
    */
   public Optional<String> getCategory() {
      return category;
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
    * The location of the blob in the blob store in which the media for the image is located. The
    * blob location belongs to a storage account in the subscription specified by the
    * <subscription-id> value in the operation call.
    * 
    * Example:
    * 
    * http://example.blob.core.windows.net/disks/myimage.vhd
    */
   public Optional<URI> getMediaLink() {
      return mediaLink;
   }

   /**
    * The eula for the image, if available.
    */
   public Optional<URI> getEula() {
      return eula;
   }

   /**
    * The description of the image.
    */
   public String getLabel() {
      return label;
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
      OSImage other = (OSImage) obj;
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
               .add("logicalSizeInGB", logicalSizeInGB.orNull()).add("description", description)
               .add("category", category.orNull()).add("location", location.orNull())
               .add("affinityGroup", affinityGroup.orNull()).add("mediaLink", mediaLink.orNull())
               .add("eula", eula.orNull()).add("label", label);
   }

}
