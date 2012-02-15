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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorConstants.VCLOUD_1_5_NS;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;


/**
 * 
 *                 Represents a media.
 *             
 * 
 * <p>Java class for Media complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Media">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}ResourceEntityType">
 *       &lt;sequence>
 *         &lt;element name="Owner" type="{http://www.vmware.com/vcloud/v1.5}OwnerType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="imageType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="size" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(namespace = VCLOUD_1_5_NS, name = "Media")
@XmlType(propOrder = {"owner"})
public class Media
    extends ResourceEntityType<Media>

{
   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

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


      public Media build() {
         Media media = new Media();
         media.setOwner(owner);
         media.setImageType(imageType);
         media.setSize(size);
         return media;
      }
      
      /**
       * @see ResourceEntityType#getFiles()
       */
      public Builder files(FilesList files) {
         super.files(files);
         return this;
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
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
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         super.tasksInProgress(tasksInProgress);
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
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
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
         return fromResourceEntityType(in)
            .owner(in.getOwner())
            .imageType(in.getImageType())
            .size(in.getSize());
      }
   }

   public Media() {
      super();
   }

    @XmlElement(namespace = VCLOUD_1_5_NS, name = "Owner")
    protected Owner owner;
    @XmlAttribute(required = true)
    protected String imageType;
    @XmlAttribute(required = true)
    protected long size;

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link Owner }
     *     
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Owner }
     *     
     */
    public void setOwner(Owner value) {
        this.owner = value;
    }

    /**
     * Gets the value of the imageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the value of the imageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageType(String value) {
        this.imageType = value;
    }

    /**
     * Gets the value of the size property.
     * 
     */
    public long getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     */
    public void setSize(long value) {
        this.size = value;
    }

   @Override
   public boolean equals(Object o) {
      if (this == o)
          return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Media that = Media.class.cast(o);
      return equal(owner, that.owner) && 
           equal(imageType, that.imageType) && 
           equal(size, that.size);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(owner, 
           imageType, 
           size);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("")
            .add("owner", owner)
            .add("imageType", imageType)
            .add("size", size).toString();
   }

}
