/**
f * Licensed to jclouds, Inc. (jclouds) under one or more
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

import java.util.Arrays;
import java.util.List;

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
public class Media extends ResourceEntityType {

   public static final class ImageType {
      public static final String ISO = "iso";
      public static final String FLOPPY = "floppy";

      public static final List<String> ALL = Arrays.asList(ISO, FLOPPY);
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromMedia(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static abstract class Builder<B extends Builder<B>> extends ResourceEntityType.Builder<B> {

      private Owner owner;
      private String imageType;
      private long size;

      /**
       * @see Media#getOwner()
       */
      public B owner(Owner owner) {
         this.owner = owner;
         return self();
      }

      /**
       * @see Media#getImageType()
       */
      public B imageType(String imageType) {
         this.imageType = imageType;
         return self();
      }

      /**
       * @see Media#getSize()
       */
      public B size(long size) {
         this.size = size;
         return self();
      }

      @Override
      public Media build() {
         return new Media(this);
      }

      public B fromMedia(Media in) {
         return fromResourceEntityType(in).owner(in.getOwner()).imageType(in.getImageType()).size(in.getSize());
      }
   }


   public Media(Builder<?> builder) {
      super(builder);
      this.owner = builder.owner;
      this.imageType = builder.imageType;
      this.size = builder.size;
   }

   protected Media() {
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
      return Objects.hashCode(super.hashCode(), owner, imageType, size);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("owner", owner).add("imageType", imageType).add("size", size);
   }

}
