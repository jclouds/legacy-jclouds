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
 * Basic entity type in the vCloud object model.
 * <p/>
 * Includes a name, an optional description, and an optional list of links
 * <p/>
 * <pre>
 * &lt;xs:complexType name="EntityType"&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 * @author Adam Lowe
 */
public abstract class EntityType<T extends EntityType<T>> extends ResourceType<T> {

   public static abstract class Builder<T extends EntityType<T>> extends ResourceType.Builder<T> {

      protected String description;
      protected TasksInProgress tasksInProgress;
      protected String name;
      protected String id;

      /**
       * @see EntityType#getName()
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      public Builder<T> description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      public Builder<T> tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ResourceType#getHref()
       */
      @Override
      public Builder<T> href(URI href) {
         this.href = href;
         return this;
      }

      /**
       * @see ResourceType#getType()
       */
      @Override
      public Builder<T> type(String type) {
         this.type = type;
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> links(Set<Link> links) {
         this.links = Sets.newLinkedHashSet(checkNotNull(links, "links"));
         return this;
      }

      /**
       * @see ResourceType#getLinks()
       */
      @Override
      public Builder<T> link(Link link) {
         this.links.add(checkNotNull(link, "link"));
         return this;
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      public Builder<T> fromResourceType(ResourceType<T> in) {
         return Builder.class.cast(super.fromResourceType(in));
      }

      public Builder<T> fromEntityType(EntityType<T> in) {
         return fromResourceType(in)
               .description(in.getDescription()).tasksInProgress(in.getTasksInProgress())
               .id(in.getId()).name(in.getName());
      }
   }

   @XmlElement(name = "Description")
   private String description;
   @XmlElement(name = "TasksInProgress")
   private TasksInProgress tasksInProgress;
   @XmlAttribute
   private String id;
   @XmlAttribute(required = true)
   private String name;

   public EntityType(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id, String name) {
      super(href, type, links);
      this.description = description;
      this.tasksInProgress = tasksInProgress;
      this.id = id;
      this.name = name;
   }

   protected EntityType() {
      // For JAXB
   }

   /**
    * Optional description.
    */
   public String getDescription() {
      return description;
   }

   /**
    * A list of queued, running, or recently completed tasks associated with this entity.
    */
   public TasksInProgress getTasksInProgress() {
      return tasksInProgress;
   }

   /**
    * The resource identifier, expressed in URN format.
    * <p/>
    * The value of this attribute uniquely identifies the resource, persists for the life of the
    * resource, and is never reused.
    */
   public String getId() {
      return id;
   }

   /**
    * Contains the name of the the entity.
    */
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      EntityType<?> that = EntityType.class.cast(o);
      return super.equals(that) &&
            equal(this.id, that.id) && equal(this.description, that.description) &&
            equal(this.tasksInProgress, that.tasksInProgress) && equal(this.name, that.name);
   }
   
   @Override
   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      EntityType<?> that = EntityType.class.cast(o);
      return super.equals(that) &&
            equal(this.description, that.description) &&
            equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(description, tasksInProgress, id, name);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("description", description).add("tasksInProgress", tasksInProgress).add("id", id).add("name", name);
   }
}