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
package org.jclouds.vcloud.director.v1_5.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.Entity;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Predicates for working with {@link EntityType} collections.
 * 
 * @author grkvlt@apache.org, Adrian Cole
 */
public class EntityPredicates {
   
   /**
    * Matches {@link EntityType entities} with the given id.
    * 
    * @param T type of the entity, for example {@link Vm}
    * @param id value of the id attribute of the entity
    * @return predicate that will match entities of the given id
    */
   public static <T extends Entity> Predicate<T> idEquals(final String id) {
      checkNotNull(id, "id must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T entity) {
            return id.equals(entity.getId());
         }

         @Override
         public String toString() {
            return "idEquals(" + id + ")";
         }
      };
   }

   /**
    * Matches {@link EntityType entities} with the given name.
    * 
    * @param T type of the entity, for example {@link Vm}
    * @param name value of the name attribute of the entity
    * @return predicate that will match entities of the given name
    */
   public static <T extends Entity> Predicate<T> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T entity) {
            return name.equals(entity.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }

   /**
    * Matches {@link EntityType entities} with names starting with the given prefix.
    * 
    * @param T type of the entity, for example {@link Vm}
    * @param name prefix of the name attribute of the entity
    * @return predicate that will match entities with names starting with the given prefix
    */
   public static <T extends Entity> Predicate<T> nameStartsWith(final String prefix) {
      checkNotNull(prefix, "prefix must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T entity) {
            String name = entity.getName();
            return name != null && name.startsWith(prefix);
         }

         @Override
         public String toString() {
            return "nameStartsWith(" + prefix + ")";
         }
      };
   }

   /**
    * Matches {@link EntityType entities} with names in the given collection.
    *
    * @param T type of the entity, for example {@link Vm}
    * @param names collection of values for the name attribute of the entity
    * @return predicate that will match entities with names starting with the given prefix
    */
   public static <T extends Entity> Predicate<T> nameIn(final Iterable<String> names) {
      checkNotNull(names, "names must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T entity) {
            String name = entity.getName();
            return Iterables.contains(names, name);
         }

         @Override
         public String toString() {
            return "nameIn(" + Iterables.toString(names) + ")";
         }
      };
   }

   /**
    * Matches {@link EntityType entities} of the given type.
    * 
    * @param T type of the entity, for example {@link Vm}
    * @param type the media type string of the entity, for example {@link VCloudDirectorMediaType#CATALOG}
    * @return predicate that will match entities of the given type
    * @see VCloudDirectorMediaType
    */
   public static <T extends Entity> Predicate<T> typeEquals(final String type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T entity) {
            return type.equals(entity.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }

   /**
    * Matches {@link EntityType entities} with the given {@link URI}.
    * 
    * @param T type of the entity, for example {@link Vm}
    * @param  href the URI of the entity
    * @return predicate that will match entities with the given URI
    * @see VCloudDirectorMediaType
    */
   public static <T extends Entity> Predicate<T> hrefEquals(final URI href) {
      checkNotNull(href, "href must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T entity) {
            return href.equals(entity.getHref());
         }

         @Override
         public String toString() {
            return "hrefEquals(" + href.toASCIIString() + ")";
         }
      };
   }
}
