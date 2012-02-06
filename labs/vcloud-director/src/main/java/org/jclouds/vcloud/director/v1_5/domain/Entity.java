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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import java.net.URI;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Basic entity type in the vCloud object model.
 *
 * Includes a name, an optional description, and an optional list of links
 *
 * <pre>
 * &lt;xs:complexType name="EntityType"&gt;
 * </pre>
 *
 * @author Adrian Cole
 */
public class Entity<T extends Entity<T>> extends Resource<T> {

   public static <T extends Entity<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   @Override
   public Builder<T> toBuilder() {
      return new Builder<T>().fromEntity(this);
   }

   public static class Builder<T extends Entity<T>> extends Resource.Builder<T> {

      protected String description;
      protected TaskList tasks;
      protected String name;
      protected String id;

      /**
       * @see Entity#getName()
       */
      public Builder<T> name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see Entity#getDescription()
       */
      public Builder<T> description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see Entity#getId()
       */
      public Builder<T> id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Entity#getTasks()
       */
      public Builder<T> tasks(TaskList tasks) {
         this.tasks = tasks;
         return this;
      }

      @Override
      public Entity<T> build() {
         Entity<T> entity = new Entity<T>(href, name);
         entity.setDescription(description);
         entity.setTasks(tasks);
         entity.setId(id);
         entity.setType(type);
         entity.setLinks(links);
         return entity;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder<T> fromResource(Resource<T> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      public Builder<T> fromEntity(Entity<T> in) {
         return fromResource(in).description(in.getDescription()).tasks(in.getTasks()).id(in.getId()).name(in.getName());
      }
   }

   @XmlElement(namespace = NS, name = "Description")
   protected String description;
   @XmlElement(namespace = NS, name = "TasksInProgress")
   protected TaskList tasks;
   @XmlAttribute
   protected String id;
   @XmlAttribute
   protected String name;

   protected Entity(URI href, String name) {
      super(href);
      this.name = name;
   }

   protected Entity() {
      // For JAXB
   }

   /**
    * Optional description.
    */
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   /**
    * A list of queued, running, or recently completed tasks associated with this entity.
    */
   public TaskList getTasks() {
      return tasks;
   }

   public void setTasks(TaskList tasks) {
      this.tasks = tasks;
   }

   /**
    * The resource identifier, expressed in URN format.
    *
    * The value of this attribute uniquely identifies the resource, persists for the life of the
    * resource, and is never reused.
    */
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   /**
    * Contains the name of the the entity.
    *
    * The object type, specified as a MIME content type, of the object that the link references.
    * This attribute is present only for links to objects. It is not present for links to actions.
    * 
    * @return type definition, type, expressed as an HTTP Content-Type
    */
   public String getName() {
      return name;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Entity<?> that = Entity.class.cast(o);
      return super.equals(that) && equal(this.id, that.id) && equal(this.description, that.description) && equal(this.tasks, that.tasks);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(description, tasks, id, name);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("description", description).add("tasks", tasks).add("id", id).add("name", name);
   }
}