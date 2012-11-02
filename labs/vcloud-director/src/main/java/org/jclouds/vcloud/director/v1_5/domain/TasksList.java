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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Task List
 * 
 * 
 * @author Adrian Cole
 */
@XmlRootElement(name = "TasksList")
public class TasksList extends Resource implements Set<Task> {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   @Override
   public Builder<?> toBuilder() {
      return builder().fromTasksList(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public abstract static class Builder<B extends Builder<B>> extends Resource.Builder<B> {
      private String name;
      private Set<Task> tasks;

      /**
       * @see TasksList#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see TasksList#getTasks()
       */
      public B tasks(Set<Task> tasks) {
         if (checkNotNull(tasks, "tasks").size() > 0)
            this.tasks = Sets.newLinkedHashSet(tasks);
         return self();
      }

      /**
       * @see TasksList#getTasks()
       */
      public B task(Task task) {
         if (tasks == null)
            tasks = Sets.newLinkedHashSet();
         this.tasks.add(checkNotNull(task, "task"));
         return self();
      }
      
      @Override
      public TasksList build() {
         return new TasksList(this);
      }

      public B fromTasksList(TasksList in) {
         return fromResource(in).tasks(in);
      }
   }

   @XmlAttribute(required = true)
   private String name;
   @XmlElement(name = "Task")
   private Set<Task> tasks;

   public TasksList(Builder<?> builder) {
      super(builder);
      this.tasks = builder.tasks;
      this.name = builder.name;
   }

   protected TasksList() {
      // For JAXB
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
      TasksList that = TasksList.class.cast(o);
      return super.equals(that) && equal(this.delegate(), that.delegate()) && equal(this.name, that.name);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), delegate(), name);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("name", name).add("tasks", delegate());
   }

   /*
    * Methods below are for implementing Set; annoying lack of multiple inheritance for using ForwardingSet!
    */
   
   private Set<Task> delegate() {
      return tasks == null ? ImmutableSet.<Task>of() : Collections.unmodifiableSet(tasks);
   }
   
   @Override
   public Iterator<Task> iterator() {
      return delegate().iterator();
   }

   @Override
   public int size() {
      return delegate().size();
   }

   @Override
   public boolean removeAll(Collection<?> collection) {
      return delegate().removeAll(collection);
   }

   @Override
   public boolean isEmpty() {
      return delegate().isEmpty();
   }

   @Override
   public boolean contains(Object object) {
      return delegate().contains(object);
   }

   @Override
   public boolean add(Task element) {
      return delegate().add(element);
   }

   @Override
   public boolean remove(Object object) {
      return delegate().remove(object);
   }

   @Override
   public boolean containsAll(Collection<?> collection) {
      return delegate().containsAll(collection);
   }

   @Override
   public boolean addAll(Collection<? extends Task> collection) {
      return delegate().addAll(collection);
   }

   @Override
   public boolean retainAll(Collection<?> collection) {
      return delegate().retainAll(collection);
   }

   @Override
   public void clear() {
      delegate().clear();
   }

   @Override
   public Object[] toArray() {
      return delegate().toArray();
   }

   @Override
   public <T> T[] toArray(T[] array) {
      return delegate().toArray(array);
   }

}
