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
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Base type that represents a resource entity such as a vApp template or virtual media.
 * <p/>
 * <pre>
 * &lt;complexType name="ResourceEntity" &gt;
 * </pre>
 *
 * @author danikov
 * @author Adam Lowe
 */
public abstract class ResourceEntityType<T extends ResourceEntityType<T>> extends EntityType<T> {

   public static abstract class Builder<T extends ResourceEntityType<T>> extends EntityType.Builder<T> {
      protected FilesList files;
      protected Integer status;

      /**
       * @see ResourceEntityType#getFiles()
       */
      public Builder<T> files(FilesList files) {
         this.files = files;
         return this;
      }

      /**
       * @see ResourceEntityType#getStatus()
       */
      public Builder<T> status(Integer status) {
         this.status = status;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }
      
      /**
       * @see EntityType#getTasks()
       */
      @Override
      public Builder<T> tasks(Set<Task> tasks) {
         super.tasks(tasks);
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ReferenceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceEntityType#getLinks()
       */
      public Builder<T> links(Set<Link> links) {
         super.links(links);
         return this;
      }

      /**
       * @see ResourceEntityType#getLinks()
       */
      public Builder<T> link(Link link) {
         super.link(link);
         return this;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromResourceType(ResourceType<T> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder<T> fromResourceEntityType(ResourceEntityType<T> in) {
         return fromResourceType(in).files(in.getFiles()).status(in.getStatus());
      }
   }

   @XmlElement(name = "Files")
   protected FilesList files;
   @XmlAttribute
   protected Integer status;

   public ResourceEntityType(URI href, String type, Set<Link> links, String description, Set<Task> tasks, String id, String name, FilesList files, Integer status) {
      super(href, type, links, description, tasks, id, name);
      this.files = files;
      this.status = status;
   }

   protected ResourceEntityType() {
      // for JAXB
   }

   
   /**
    * Gets the value of the files property.
    */
   public FilesList getFiles() {
      return files;
   }

   /**
    * Gets the value of the status property.
    */
   public Integer getStatus() {
      return status;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceEntityType<?> that = ResourceEntityType.class.cast(o);
      return super.equals(that) && equal(this.files, that.files) && equal(this.status, that.status);
   }
   
   @Override
   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      ResourceEntityType<?> that = ResourceEntityType.class.cast(o);
      return super.clone(that) && equal(this.files, that.files);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(files, status);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("files", files).add("status", status);
   }

}
