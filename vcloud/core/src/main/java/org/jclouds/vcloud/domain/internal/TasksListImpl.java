/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.domain.internal;

import java.net.URI;
import java.util.SortedSet;

import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

/**
 * Locations of resources in vCloud
 * 
 * @author Adrian Cole
 * 
 */
public class TasksListImpl implements TasksList {
   private final String id;
   private final SortedSet<Task> tasks;
   private final ListMultimap<URI, Task> tasksByResult;
   private final ListMultimap<URI, Task> tasksByOwner;

   private final URI location;
   /** The serialVersionUID */
   private static final long serialVersionUID = 8464716396538298809L;

   public TasksListImpl(String id, URI location, SortedSet<Task> tasks) {
      this.id = id;
      this.location = location;
      this.tasks = tasks;
      this.tasksByResult = Multimaps.index(Iterables.filter(tasks, new Predicate<Task>() {

         public boolean apply(Task input) {
            return input.getResult() != null;
         }

      }), new Function<Task, URI>() {

         public URI apply(Task input) {

            return input.getResult().getLocation();
         }

      });
      this.tasksByOwner = Multimaps.index(Iterables.filter(tasks, new Predicate<Task>() {

         @Override
         public boolean apply(Task input) {
            return input.getOwner() != null;
         }

      }), new Function<Task, URI>() {
         public URI apply(Task in) {
            return in.getOwner().getLocation();
         }
      });
   }

   public SortedSet<Task> getTasks() {
      return tasks;
   }

   public URI getLocation() {
      return location;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((location == null) ? 0 : location.hashCode());
      result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
      result = prime * result + ((tasksByOwner == null) ? 0 : tasksByOwner.hashCode());
      result = prime * result + ((tasksByResult == null) ? 0 : tasksByResult.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      TasksListImpl other = (TasksListImpl) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (location == null) {
         if (other.location != null)
            return false;
      } else if (!location.equals(other.location))
         return false;
      if (tasks == null) {
         if (other.tasks != null)
            return false;
      } else if (!tasks.equals(other.tasks))
         return false;
      if (tasksByOwner == null) {
         if (other.tasksByOwner != null)
            return false;
      } else if (!tasksByOwner.equals(other.tasksByOwner))
         return false;
      if (tasksByResult == null) {
         if (other.tasksByResult != null)
            return false;
      } else if (!tasksByResult.equals(other.tasksByResult))
         return false;
      return true;
   }

   public ListMultimap<URI, Task> getTasksByResult() {
      return tasksByResult;
   }

   public ListMultimap<URI, Task> getTasksByOwner() {
      return tasksByOwner;
   }

   @Override
   public String getId() {
      return id;
   }
}