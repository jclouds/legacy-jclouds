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

import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Predicates for working with {@link Reference} collections.
 * 
 * @author Adrian Cole
 */
public class ReferencePredicates {

   /**
    * Matches {@link Reference}s with the given name.
    * 
    * @param T type of the reference, for example {@link Link}
    * @param name value of the name attribute of the referenced object
    * @return predicate that will match references of the given name
    */
   public static <T extends Reference> Predicate<T> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T reference) {
            return name.equals(reference.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }

   /**
    * Matches {@link Reference}s with names starting with the given prefix.
    * 
    * @param T type of the reference, for example {@link Link}
    * @param name prefix of the name attribute of the referenced object
    * @return predicate that will match references with names starting with the given prefix
    */
   public static <T extends Reference> Predicate<T> nameStartsWith(final String prefix) {
      checkNotNull(prefix, "prefix must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T reference) {
            String name = reference.getName();
            return name != null && name.startsWith(prefix);
         }

         @Override
         public String toString() {
            return "nameStartsWith(" + prefix + ")";
         }
      };
   }

   /**
    * Matches {@link Reference}s with names in the given collection.
    *
    * @param T type of the reference, for example {@link Link}
    * @param names collection of values for the name attribute of the referenced object
    * @return predicate that will match references with names starting with the given prefix
    */
   public static <T extends Reference> Predicate<T> nameIn(final Iterable<String> names) {
      checkNotNull(names, "names must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T reference) {
            String name = reference.getName();
            return Iterables.contains(names, name);
         }

         @Override
         public String toString() {
            return "nameIn(" + Iterables.toString(names) + ")";
         }
      };
   }

   /**
    * Matches {@link Reference}s of the given type.
    * 
    * @param T type of the reference, for example {@link Link}
    * @param type the media type string of the referenced object, for example {@link VCloudDirectorMediaType#CATALOG}
    * @return predicate that will match references of the given type
    * @see VCloudDirectorMediaType
    */
   public static <T extends Reference> Predicate<T> typeEquals(final String type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T reference) {
            return type.equals(reference.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }
}
