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
import java.util.Collections;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A list of tasks.
 *
 * @author Adrian Cole
 */
@XmlRootElement(name = "TasksList")
public class TasksList extends EntityType<TasksList> {

   public static final String MEDIA_TYPE = VCloudDirectorMediaType.TASKS_LIST;

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder extends EntityType.Builder<TasksList> {

      protected Set<Task> tasks = Sets.newLinkedHashSet();

      /**
       * @see TasksList#getTasks()
       */
      public Builder tasks(Set<Task> tasks) {
         this.tasks = Sets.newLinkedHashSet(checkNotNull(tasks, "tasks"));
         return this;
      }

      /**
       * @see TasksList#getTasks()
       */
      public Builder task(Task task) {
         this.tasks.add(checkNotNull(task, "task"));
         return this;
      }

      @Override
      public TasksList build() {
         return new TasksList(href, type, links, description, tasksInProgress, id, name, tasks);
      }

      /**
       * @see EntityType#getName()
       */
      @Override
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see EntityType#getDescription()
       */
      @Override
      public Builder description(String description) {
         this.description = description;
         return this;
      }

      /**
       * @see EntityType#getId()
       */
      @Override
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see EntityType#getTasksInProgress()
       */
      @Override
      public Builder tasksInProgress(TasksInProgress tasksInProgress) {
         this.tasksInProgress = tasksInProgress;
         return this;
      }

      /**
       * @see ReferenceType#getHref()
       */
      @Override
      public Builder href(URI href) {
         this.href = href;
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
      public Builder fromEntityType(EntityType<TasksList> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromTasksList(TasksList in) {
         return fromEntityType(in).tasks(in.getTasks());
      }
   }

   protected TasksList() {
      // For JAXB and builder use
   }

   public TasksList(URI href, String type, Set<Link> links, String description, TasksInProgress tasksInProgress, String id, String name, Set<Task> tasks) {
      super(href, type, links, description, tasksInProgress, id, name);
      this.tasks = ImmutableSet.copyOf(tasks);
   }

   @XmlElement(name = "Task")
   private Set<Task> tasks = Sets.newLinkedHashSet();

   public Set<Task> getTasks() {
      return Collections.unmodifiableSet(tasks);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      TasksList that = TasksList.class.cast(o);
      return super.equals(that) && equal(this.tasks, that.tasks);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(tasks);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("tasks", tasks);
   }
}
