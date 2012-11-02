/*
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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Basic entity type in the vCloud object model.
 *
 * Includes the entity name and an optional id, description, and set of running {@link Task}s.
 *
 * <pre>
 * &lt;xs:complexType name="EntityType" /&gt;
 * </pre>
 *
 * @author grkvlt@apache.org
 * @author Adam Lowe
 */
@XmlRootElement(name = "Entity")
@XmlType(name = "EntityType")
public class Entity extends Resource {
   
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromEntityType(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      
      private String description;
      private List<Task> tasks = Lists.newArrayList();
      private String name;
      private String id;

      /**
       * @see EntityType#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see EntityType#getDescription()
       */
      public B description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see EntityType#getId()
       */
      public B id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see EntityType#getTasks()
       */
      public B tasks(Iterable<Task> tasks) {
         this.tasks = Lists.newArrayList(checkNotNull(tasks, "tasks"));
         return self();
      }

      /**
       * @see EntityType#getTasks()
       */
      public B task(Task task) {
         this.tasks.add(checkNotNull(task, "task"));
         return self();
      }

      @Override
      public Entity build() {
         return new Entity(this);
      }
      
      public B fromEntityType(Entity in) {
         return fromResource(in)
               .description(in.getDescription())
               .tasks(in.getTasks())
               .id(in.getId()).name(in.getName());
      }
   }

   @XmlElement(name = "Description")
   private String description;
   @XmlElementWrapper(name = "Tasks")
   @XmlElement(name = "Task")
   private List<Task> tasks;
   @XmlAttribute
   private String id;
   @XmlAttribute(required = true)
   private String name;

   protected Entity(Builder<?> builder) {
      super(builder);
      this.description = builder.description;
      this.tasks = builder.tasks == null || builder.tasks.isEmpty() ? null : ImmutableList.copyOf(builder.tasks);
      this.id = builder.id;
      this.name = builder.name;
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
   public List<Task> getTasks() {
      return tasks == null ? ImmutableList.<Task>of() : ImmutableList.copyOf(tasks);
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

   /**
    * Contains the name of the the entity.
    */
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Entity that = Entity.class.cast(o);
      return super.equals(that) &&
            equal(this.id, that.id) && equal(this.description, that.description) &&
            equal(this.tasks, that.tasks) && equal(this.name, that.name);
   }
   
   @Override
   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      Entity that = Entity.class.cast(o);
      return super.clone(that);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), description, tasks, id, name);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("description", description).add("tasks", tasks).add("id", id).add("name", name);
   }
}
