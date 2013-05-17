/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;

/**
 * @author Adrian Cole
 */
public final class IdAndName {

   public static IdAndName create(String id, String name) {
      return new IdAndName(id, name);
   }

   private final String id;
   private final String name;

   private IdAndName(String id, String name) {
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name for %s", id);
   }

   /**
    * The id of the resource. ex {@code AAAAAAAAAAAAAAAA}
    */
   public String getId() {
      return id;
   }

   /**
    * The name of the resource. ex {@code jclouds}
    */
   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      IdAndName that = IdAndName.class.cast(obj);
      return Objects.equal(this.id, that.id) && Objects.equal(this.name, that.name);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("id", id).add("name", name).toString();
   }

   /**
    * convenience predicate as typically the user is unaware of the system
    * generated id of a resource
    * 
    * @param name
    *           see {@link #getName()}
    */
   public static Predicate<IdAndName> nameEqualTo(String name) {
      return compose(equalTo(name), ToName.INSTANCE);
   }

   private static enum ToName implements Function<IdAndName, String> {
      INSTANCE;
      @Override
      public String apply(IdAndName input) {
         return input.getName();
      }
   }
}
