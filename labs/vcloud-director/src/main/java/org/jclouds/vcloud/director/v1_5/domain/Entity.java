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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * An entity.
 *
 * @author grkvlt@apache.org
 * @author Adam Lowe
 */
public class Entity extends EntityType<Entity> {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return new Builder();
   }

   public static class Builder extends EntityType.Builder<Entity> {

      @Override
      public Entity build() {
         return new Entity(href, type, links, description, tasks, id, name);
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
       * @see EntityType#getTasks()
       */
      @Override
      public Builder tasks(Set<Task> tasks) {
         super.tasks(tasks);
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
      public Builder fromEntityType(EntityType<Entity> in) {
         return Builder.class.cast(super.fromEntityType(in));
      }

      public Builder fromEntity(Entity in) {
         return fromEntityType(in);
      }
   }

   private Entity(URI href, String type, Set<Link> links, String description, Set<Task> tasks, String id, String name) {
      super(href, type, links, description, tasks, id, name);
   }

   private Entity() {
      // For JAXB
   }
}