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
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;


/**
 * Represents a file to be transferred (uploaded or downloaded).
 * <p/>
 * <p/>
 * <p>Java class for File complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="File">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.vmware.com/vcloud/v1.5}EntityType">
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="bytesTransferred" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="checksum" type="{http://www.w3.org/2001/XMLSchema}normalizedString" />
 *       &lt;anyAttribute processContents='lax' namespace='##other'/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlType(name = "File")
public class File extends EntityType<File> {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder().fromFile(this);
   }

   public static class Builder extends EntityType.Builder<File> {

      private Long size;
      private Long bytesTransferred;
      private String checksum;

      /**
       * @see File#getSize()
       */
      public Builder size(Long size) {
         this.size = size;
         return this;
      }

      /**
       * @see File#getBytesTransferred()
       */
      public Builder bytesTransferred(Long bytesTransferred) {
         this.bytesTransferred = bytesTransferred;
         return this;
      }

      /**
       * @see File#getChecksum()
       */
      public Builder checksum(String checksum) {
         this.checksum = checksum;
         return this;
      }

      public File build() {
         return new File(href, type, links, description, tasksInProgress, id, name, size, bytesTransferred, checksum);

      }

      /**
       * @see EntityType#getName()
       */
      public Builder name(String name) {
         super.name(name);
         return this;
      }
      
      /**
       * @see EntityType#getDescription()
       */
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
         super.tasksInProgress (tasksInProgress);
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
         this.type = type;
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }


      @Override
      public Builder fromEntityType(EntityType<File> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromFile(File in) {
         return fromEntityType(in)
               .size(in.getSize())
               .bytesTransferred(in.getBytesTransferred())
               .checksum(in.getChecksum());
      }
   }

   public File(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id,
               String name, Long size, Long bytesTransferred, String checksum) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.size = size;
      this.bytesTransferred = bytesTransferred;
      this.checksum = checksum;
   }

   @SuppressWarnings("unused")
   private File() {
      // For JAXB
   }

   @XmlAttribute
   protected Long size;
   @XmlAttribute
   protected Long bytesTransferred;
   @XmlAttribute
   @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
   @XmlSchemaType(name = "normalizedString")
   protected String checksum;

   /**
    * Gets the value of the size property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getSize() {
      return size;
   }

   /**
    * Gets the value of the bytesTransferred property.
    *
    * @return possible object is
    *         {@link Long }
    */
   public Long getBytesTransferred() {
      return bytesTransferred;
   }

   /**
    * Gets the value of the checksum property.
    *
    * @return possible object is
    *         {@link String }
    */
   public String getChecksum() {
      return checksum;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      File that = File.class.cast(o);
      return super.equals(that) &&
           equal(size, that.size) && 
            equal(bytesTransferred, that.bytesTransferred) &&
            equal(checksum, that.checksum);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(),
           size, 
            bytesTransferred,
            checksum);
   }

   @Override
   public ToStringHelper string() {
      return super.string()
            .add("size", size)
            .add("bytesTransferred", bytesTransferred)
            .add("checksum", checksum);
   }

}
