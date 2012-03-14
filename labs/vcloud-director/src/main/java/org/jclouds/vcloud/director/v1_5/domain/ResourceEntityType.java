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

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorRuntimeException;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Base type that represents a resource entity such as a vApp template or virtual media.
 *
 * <pre>
 * &lt;complexType name="ResourceEntity" /&gt;
 * </pre>
 *
 * @author danikov
 * @author Adam Lowe
 * @author grkvlt@apache.org
 */
@XmlType(name = "ResourceEntityType")
public abstract class ResourceEntityType<T extends ResourceEntityType<T>> extends EntityType<T> {

   public static enum Status {
      
      FAILED_CREATION(-1, "The object could not be created.", true, true, true),
      UNRESOLVED(0, "The object is unresolved.", true, true, true),
      RESOLVED(1, "The object is resolved.", true, true, true),
      DEPLOYED(2, "The object is deployed.", false, false, false),
      SUSPENDED(3, "The object is suspended.", false, true, true),
      POWERED_ON(4, "The object is powered on.", false, true, true),
      WAITING_FOR_INPUT(5, "The object is waiting for user input.", false, true, true),
      UNKNOWN(6, "The object is in an unknown state.", true, true, true),
      UNRECOGNIZED(7, "The object is in an unrecognized state.", true, true, true),
      POWERED_OFF(8, "The object is powered off.", true, true, true),
      INCONSISTENT_STATE(9, "The object is in an inconsistent state.", false, true, true),
      MIXED(10, "Children do not all have the same status.", true, true, false),
      UPLOAD_OVF_PENDING(11, "Upload initiated, OVF descriptor pending.", true, false, false),
      UPLOAD_COPYING(12, "Upload initiated, copying contents.", true, false, false),
      UPLOAD_DISK_PENDING(13, "Upload initiated , disk contents pending.", true, false, false),
      UPLOAD_QUARANTINED(14, "Upload has been quarantined.", true, false, false),
      UPLOAD_QUARANTINE_EXPIRED(15, "Upload quarantine period has expired.", true, false, false);
      
      private Integer value;
      private String description;
      private boolean vAppTemplate;
      private boolean vApp;
      private boolean vm;
      
      private Status(int value, String description, boolean vAppTemplate, boolean vApp, boolean vm) {
         this.value = value;
         this.description = description;
         this.vAppTemplate = vAppTemplate;
         this.vApp = vApp;
         this.vm = vm;
      }

      public Integer getValue() {
         return value;
      }

      public String getDescription() {
         return description;
      }

      public boolean isVAppTemplate() {
         return vAppTemplate;
      }

      public boolean isVApp() {
         return vApp;
      }

      public boolean isVm() {
         return vm;
      }

      public static Status fromValue(final int value) {
         Optional<Status> found = Iterables.tryFind(Arrays.asList(values()), new Predicate<Status>() {
            @Override
            public boolean apply(Status status) {
               return status.getValue() == value;
            }
         });
         if (found.isPresent()) {
            return found.get();
         } else {
            throw new VCloudDirectorRuntimeException(String.format("Illegal status value '%d'", value));
         }
      }
   }
       
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
       * @see EntityType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         if (checkNotNull(links, "links").size() > 0)
            this.links = Sets.newLinkedHashSet(links);
         return this;
      }

      /**
       * @see EntityType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         if (links == null)
            links = Sets.newLinkedHashSet();
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      @Override
      public Builder<T> fromEntityType(EntityType<T> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder<T> fromResourceEntityType(ResourceEntityType<T> in) {
         return fromEntityType(in).files(in.getFiles()).status(in.getStatus());
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
      return Objects.hashCode(super.hashCode(), files, status);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("files", files).add("status", status);
   }

}
