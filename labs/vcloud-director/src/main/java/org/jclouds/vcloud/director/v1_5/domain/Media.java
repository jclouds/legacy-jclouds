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
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Represents a media.
 * <p/>
 * <pre>
 * &lt;complexType name="Media" /&gt;
 * </pre>
 */
@XmlRootElement(name = "Media")
public class Media extends ResourceEntityType<Media> {

   public static final class ImageType {
      public static final String ISO = "iso";
      public static final String FLOPPY = "floppy";

      public static final List<String> ALL = Arrays.asList(ISO, FLOPPY);
   }

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromMedia(this);
   }

   public static class Builder extends ResourceEntityType.Builder<Media> {

      private Owner owner;
      private String imageType;
      private long size;

      /**
       * @see Media#getOwner()
       */
      public Builder owner(Owner owner) {
         this.owner = owner;
         return this;
      }

      /**
       * @see Media#getImageType()
       */
      public Builder imageType(String imageType) {
         this.imageType = imageType;
         return this;
      }

      /**
       * @see Media#getSize()
       */
      public Builder size(long size) {
         this.size = size;
         return this;
      }

      @Override
      public Media build() {
         return new Media(href, type, links, description, tasks, id, name, files, status, owner, imageType, size);
      }

      /**
       * @see ResourceEntityType#getFiles()
       */
      @Override
      public Builder files(FilesList files) {
         super.files(files);
         return this;
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      @Override
      public Builder status(Integer status) {
         super.status(status);
         return this;
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         super.name(name);
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         super.description(description);
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         super.id(id);
         return this;
      }

      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         super.tasks(tasks);
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         super.href(href);
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder type(String type) {
         super.type(type);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         super.link(link);
         return this;
      }

      @Override
      public Builder fromResourceEntityType(ResourceEntityType<Media> in) {
         return Builder.class.cast(super.fromResourceEntityType(in));
      }

      public Builder fromMedia(Media in) {
         return fromResourceEntityType(in).owner(in.getOwner()).imageType(in.getImageType()).size(in.getSize());
      }
   }


   public Media(URI href, String type, Set<Link> links, String description, Set<Task> tasks, String id, 
                String name, FilesList files, Integer status, Owner owner, String imageType, long size) {
      super(href, type, links, description, tasks, id, name, files, status);
      this.owner = owner;
      this.imageType = imageType;
      this.size = size;
   }

   private Media() {
      // for JAXB
   }

   @XmlElement(name = "Owner")
   protected Owner owner;
   @XmlAttribute(required = true)
   protected String imageType;
   @XmlAttribute(required = true)
   protected long size;

   /**
    * Gets the value of the owner property.
    */
   public Owner getOwner() {
      return owner;
   }

   /**
    * Gets the value of the imageType property.
    */
   public String getImageType() {
      return imageType;
   }

   /**
    * Gets the value of the size property.
    */
   public long getSize() {
      return size;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Media that = Media.class.cast(o);
      return super.equals(that) &&
            equal(this.owner, that.owner) && equal(this.imageType, that.imageType) && equal(this.size, that.size);
   }
   
   @Override
   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      Media that = Media.class.cast(o);
      return super.clone(that) && 
            equal(this.imageType, that.imageType) && equal(this.size, that.size);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(owner, imageType, size);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner).add("imageType", imageType).add("size", size);
   }

}
