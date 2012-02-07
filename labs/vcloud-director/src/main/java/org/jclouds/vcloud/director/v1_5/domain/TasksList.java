/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.task/licenses/LICENSE-2.0
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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType.*;

import java.net.URI;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * A list of tasks.
 * 
 * @author Adrian Cole
 */
@XmlRootElement(namespace = NS, name = "TasksList")
public class TasksList extends EntityType<TasksList> {

   @SuppressWarnings("unchecked")
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
         TasksList taskslist = new TasksList(href, name, tasks);
         taskslist.setDescription(description);
         taskslist.setTasksInProgress(tasksInProgress);
         taskslist.setId(id);
         taskslist.setType(type);
         taskslist.setLinks(links);
         return taskslist;
      }

      public Builder fromTasksList(TasksList in) {
         return tasks(in.getTasks());
      }
   }

   protected TasksList() {
      // For JAXB and builder use
   }

   protected TasksList(URI href, String name, Set<Task> tasks) {
      super(href, name);
      this.tasks = ImmutableSet.copyOf(tasks);
   }

   @XmlElement(namespace = NS, name = "Task")
   private Set<Task> tasks = Sets.newLinkedHashSet();

   public Set<Task> getTasks() {
      return ImmutableSet.copyOf(tasks);
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
