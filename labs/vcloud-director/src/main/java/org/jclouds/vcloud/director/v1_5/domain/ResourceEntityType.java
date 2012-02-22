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
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.Sets;

/**
 * Base type that represents a resource entity such as a vApp template or virtual media.
 * 
 * <pre>
 * &lt;complexType name="ResourceEntity" &gt;
 * </pre>
 * 
 * @author danikov
 */
public class ResourceEntityType<T extends ResourceEntityType<T>> extends EntityType<T> {

   public static <T extends ResourceEntityType<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromResourceEntityType(this);
   }

   public static class Builder<T extends ResourceEntityType<T>> extends EntityType.Builder<T> {

      private FilesList files;
      private Integer status;

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

      @Override
      public ResourceEntityType<T> build() {
         ResourceEntityType<T> resourceEntity = new ResourceEntityType<T>();
         resourceEntity.setFiles(files);
         resourceEntity.setStatus(status);
         return resourceEntity;
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
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder<T> tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
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
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ReferenceType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder<T> fromResourceType(ResourceType<T> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder<T> fromResourceEntityType(ResourceEntityType<T> in) {
         return fromResourceType(in).files(in.getFiles()).status(in.getStatus());
      }
   }

   public ResourceEntityType() {
      // for JAXB
   }

   @XmlElement(name = "Files")
   protected FilesList files;
   @XmlAttribute
   protected Integer status;

   /**
    * Gets the value of the files property.
    */
   public FilesList getFiles() {
      return files;
   }

   public void setFiles(FilesList value) {
      this.files = value;
   }

   /**
    * Gets the value of the status property.
    */
   public Integer getStatus() {
      return status;
   }

   public void setStatus(Integer value) {
      this.status = value;
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
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(files, status);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("files", files).add("status", status);
   }

}
