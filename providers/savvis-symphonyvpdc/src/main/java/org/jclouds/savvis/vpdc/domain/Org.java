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
package org.jclouds.savvis.vpdc.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A cloud can contain one or more Organizations. There are two types of Organizations, hard-walled
 * and soft-walled.
 * <ul>
 * <li>A hard-walled Organization provides a secure environment for a single tenant of a
 * multi-tenant cloud.All resources in a hard-walled Organization are isolated from other
 * Organizations, hard- or soft-walled,in the cloud.</li>
 * <li>A soft-walled Organization supports access, by users who have appropriate privileges, to
 * other soft-walled Organizations in a cloud. Soft-walled Organizations have boundaries similar to
 * those that separate departments of a corporate entity. In such environments, the Organization
 * controls the resources owned by a single department. Most users are restricted to the resources
 * available in a single Organization but a few might have privileges in other Organizations, if
 * allowed by the administrators of those Organizations.</li>
 * </ul>
 * 
 * @author Adrian Cole
 */
public class Org extends ResourceImpl {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder extends ResourceImpl.Builder {
      private String description;
      private Set<Link> vDCs = Sets.newLinkedHashSet();
      private Set<Link> images = Sets.newLinkedHashSet();

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Builder vDC(Link vDC) {
         this.vDCs.add(checkNotNull(vDC, "vDC"));
         return this;
      }

      public Builder vDCs(Set<Link> vDCs) {
         this.vDCs.addAll(checkNotNull(vDCs, "vDCs"));
         return this;
      }

      public Builder image(Link image) {
         this.images.add(checkNotNull(image, "image"));
         return this;
      }

      public Builder images(Set<Link> images) {
         this.images.addAll(checkNotNull(images, "images"));
         return this;
      }

      @Override
      public Org build() {
         return new Org(id, name, type, href, description, vDCs, images);
      }

      public static Builder fromOrg(Org in) {
         return new Builder().id(in.getId()).name(in.getName()).type(in.getType()).href(in.getHref())
               .description(in.getDescription()).images(in.getImages()).vDCs(in.getVDCs());
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

   @Nullable
   private final String description;
   private final Set<Link> vDCs;
   private final Set<Link> images;

   public Org(String id, String name, String type, URI href, @Nullable String description, Set<Link> vDCs,
         Set<Link> images) {
      super(id, name, type, href);
      this.description = description;
      this.vDCs = ImmutableSet.copyOf(checkNotNull(vDCs, "vDCs"));
      this.images = ImmutableSet.copyOf(checkNotNull(images, "images"));
   }

   /**
    * {@inheritDoc}
    */
   public String getName() {
      return name;
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
   public Set<Link> getVDCs() {
      return vDCs;
   }

   /**
    * {@inheritDoc}
    */
   public Set<Link> getImages() {
      return images;
   }

   @Override
   public Builder toBuilder() {
      return Builder.fromOrg(this);
   }

   @Override
   public String toString() {
      return "[id=" + id + ", href=" + href + ", name=" + name + ", type=" + type + ", description=" + description
            + ", vDCs=" + vDCs + ", images=" + images + "]";
   }

}