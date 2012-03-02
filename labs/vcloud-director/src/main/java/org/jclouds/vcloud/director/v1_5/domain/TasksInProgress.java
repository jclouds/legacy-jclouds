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
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIOXMLNS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author grkvlt@apache.org
 */
@XmlRootElement(name = "TasksInProgress")
public class TasksInProgress {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder {

      protected Set<Task> tasks = Sets.newLinkedHashSet();

      /**
       * @see TasksInProgress#getTasks()
       */
      public Builder tasks(Set<Task> tasks) {
         this.tasks = Sets.newLinkedHashSet(checkNotNull(tasks, "tasks"));
         return this;
      }

      /**
       * @see TasksInProgress#getTasks()
       */
      public Builder task(Task task) {
         this.tasks.add(checkNotNull(task, "task"));
         return this;
      }

      public TasksInProgress build() {
         return new TasksInProgress(tasks);
      }

      public Builder fromTasksInProgress(TasksInProgress in) {
         return tasks(in.getTasks());
      }
   }

   protected TasksInProgress() {
      // For JAXB and builder use
   }

   protected TasksInProgress(Collection<Task> tasks) {
      this.tasks = ImmutableSet.copyOf(tasks);
   }

   @XmlElement(name = "Task")
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
      TasksInProgress that = TasksInProgress.class.cast(o);
      return equal(this.tasks, that.tasks);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tasks);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("tasks", tasks).toString();
   }
}

