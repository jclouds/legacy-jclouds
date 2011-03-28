/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * VDC is a virtual data center ,the API returns a list of VAPPs own by given bill site Id.
 * 
 * @author Adrian Cole
 */
public class VDC extends ResourceImpl {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends ResourceImpl.Builder {
      private String description;
      private Status status;
      private Set<Resource> resourceEntities = Sets.newLinkedHashSet();
      private Set<Resource> availableNetworks = Sets.newLinkedHashSet();

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder status(Status status) {
         this.status = status;
         return this;
      }

      public Builder resourceEntity(Resource resourceEntity) {
         this.resourceEntities.add(checkNotNull(resourceEntity, "resourceEntity"));
         return this;
      }

      public Builder resourceEntities(Set<Resource> resourceEntities) {
         this.resourceEntities.addAll(checkNotNull(resourceEntities, "resourceEntities"));
         return this;
      }

      public Builder availableNetwork(Resource availableNetwork) {
         this.availableNetworks.add(checkNotNull(availableNetwork, "availableNetwork"));
         return this;
      }

      public Builder availableNetworks(Set<Resource> availableNetworks) {
         this.availableNetworks.addAll(checkNotNull(availableNetworks, "availableNetworks"));
         return this;
      }

      @Override
      public VDC build() {
         return new VDC(id, name, type, href, description, status, resourceEntities, availableNetworks);
      }

      public static Builder fromVDC(VDC in) {
         return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
               .status(in.getStatus()).description(in.getDescription()).availableNetworks(in.getAvailableNetworks())
               .resourceEntities(in.getResourceEntities());
      }

      @Override
      public Builder id(String id) {
         return Builder.class.cast(super.id(id));
      }

      @Override
      public Builder name(String name) {
         return Builder.class.cast(super.name(name));
      }

      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

   }

   public enum Status {
      /**
       * Savvis VPDC successfully provisioned and available for use
       */
      DEPLOYED,
      /**
       * Savvis VPDC need to be provisioned and cannot invoke any of the VCloud (post request method
       * type) API's
       */
      DESIGNING,
      /**
       * Savvis VPDC need to be provisioned and cannot invoke any of the VCloud (post request method
       * type) API's
       */
      SAVED,
      /**
       * Please wait for the provisioning process to complete and cannot invoke any of the VCloud
       * (post request method type) API's
       */
      INQUEUE,
      /**
       * Please wait for the provisioning process to complete and cannot invoke any of the VCloud
       * (post request method type) API's
       */
      PROVISIONING,
      /**
       * Please kindly contact Savvis administrator for further clarification/assistance with the
       * respective request data. and cannot invoke any of the VCloud (post request method type)
       * API's
       */
      PARTIALLY_DEPLOYED,
      /**
       * Please kindly contact Savvis administrator for further clarification/assistance with the
       * respective request data and cannot invoke any of the VCloud (post request method type)
       * API's
       */
      FAILED, UNRECOGNIZED;
      @Override
      public String toString() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   @Nullable
   private final String description;
   private final VDC.Status status;
   private final Set<Resource> resourceEntities;
   private final Set<Resource> availableNetworks;

   public VDC(String id, String name, String type, URI href, @Nullable String description, VDC.Status status,
         Set<Resource> resourceEntities, Set<Resource> availableNetworks) {
      super(id, name, type, href);
      this.description = description;
      this.status = checkNotNull(status, "status");
      this.resourceEntities = ImmutableSet.copyOf(checkNotNull(resourceEntities, "resourceEntities"));
      this.availableNetworks = ImmutableSet.copyOf(checkNotNull(availableNetworks, "availableNetworks"));
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   public VDC.Status getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   public Set<Resource> getResourceEntities() {
      return resourceEntities;
   }

   /**
    * {@inheritDoc}
    */
   public Set<Resource> getAvailableNetworks() {
      return availableNetworks;
   }

   @Override
   public Builder toBuilder() {
      return Builder.fromVDC(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + ", description=" + description
            + ", status=" + status + ", resourceEntities=" + resourceEntities + ", availableNetworks="
            + availableNetworks + "]";
   }

}