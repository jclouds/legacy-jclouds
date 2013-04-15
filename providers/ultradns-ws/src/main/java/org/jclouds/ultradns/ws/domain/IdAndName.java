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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public final class IdAndName {
   public static IdAndName fromIdAndName(String id, String name) {
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
      return Objects.toStringHelper(this).add("id", id).add("name", name).toString();
   }
}
