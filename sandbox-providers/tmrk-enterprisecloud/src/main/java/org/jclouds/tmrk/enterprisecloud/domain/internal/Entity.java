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
package org.jclouds.tmrk.enterprisecloud.domain.internal;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Link;
import org.jclouds.tmrk.enterprisecloud.domain.Task;
import org.jclouds.tmrk.enterprisecloud.domain.Tasks;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Entity class. Extends Resource with Tasks
 * <xs:complexType name="EntityType">
 * @author Jason King
 * 
 */
public class Entity<T extends Entity<T>> extends Resource<T> {

   public static <T extends Entity<T>> Builder<T> builder() {
      return new Builder<T>();
   }

   public Builder<T> toBuilder() {
      return new Builder<T>().fromResource(this);
   }

   public static class Builder<T extends Entity<T>> extends Resource.Builder<T> {

     protected Set<Task> tasks = Sets.newLinkedHashSet();

     /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.internal.Entity#getTasks
       */
      public Builder<T> tasks(Set<Task> tasks) {
         this.tasks = ImmutableSet.<Task> copyOf(checkNotNull(tasks, "tasks"));
         return this;
      }

      /**
       * @see Resource#getLinks
       */
      public Builder<T> links(Set<Link> links) {
         return Builder.class.cast(super.links(links));
      }

      /**
       * @see Resource#getActions
       */
      public Builder<T> actions(Set<Action> actions) {
         return Builder.class.cast(super.actions(actions));
      }

      /**
       * @see Resource#getActions
       */
      public Builder<T> name(String name) {
         return Builder.class.cast(super.name(name));
      }

      public Entity<T> build() {
         return new Entity<T>(href, type, name, links, actions, tasks);
      }

      /**
       * {@inheritDoc}
       */
      public Builder<T> fromBaseResource(BaseResource<T> in) {
         return Builder.class.cast(super.fromBaseResource(in));
      }

      /**
       * {@inheritDoc}
       */
      public Builder<T> fromResource(Resource<T> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      public Builder<T> fromEntity(Entity<T> in) {
         return fromResource(in).links(in.getLinks()).actions(in.getActions()).name(in.getName())
                 .tasks(in.getTasks());
      }

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      public Builder<T> fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }
   }

   @XmlElement(name = "Tasks", required = false)
   private Tasks tasks = Tasks.builder().build();

   protected Entity(URI href, String type, @Nullable String name, Set<Link> links, Set<Action> actions, Set<Task> tasks) {
      super(href, type, name, links, actions);
      this.tasks = Tasks.builder().tasks(checkNotNull(tasks,"tasks")).build();
   }

   protected Entity() {
      //For JAXB
   }

   public Set<Task> getTasks() {
      return Collections.unmodifiableSet(tasks.getTasks());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;

      Entity entity = (Entity) o;

      if (!tasks.equals(entity.tasks)) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + tasks.hashCode();
      return result;
   }

   @Override
   public String string() {
     return super.string()+", name="+name+", tasks="+tasks;
   }
}