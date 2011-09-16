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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

/**
 * Within a cloud provider a realm represents a boundary containing resources. The exact definition
 * of a realm is left to the cloud provider. In some cases, a realm may represent different
 * datacenters, different continents, or different pools of resources within a single datacenter. A
 * cloud provider may insist that resources must all exist within a single realm in order to
 * cooperate. For instance, storage volumes may only be allowed to be mounted to instances within
 * the same realm.
 * 
 * @author Adrian Cole
 */
public class Realm {
   public enum State {

      /**
       * the realm is available
       */
      AVAILABLE,

      /**
       * the realm is unavailable
       */
      UNAVAILABLE,

      /**
       * state returned as something besides the above.
       */
      UNRECOGNIZED;

      public static State fromValue(String state) {
         try {
            return valueOf(checkNotNull(state, "state"));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }
   private final URI href;
   private final String id;
   @Nullable
   private final String limit;
   private final String name;
   private final State state;

   public Realm(URI href, String id, String name, @Nullable String limit, State state) {
      this.href = checkNotNull(href, "href");
      this.id = checkNotNull(id, "id");
      this.name = checkNotNull(name, "name");
      this.limit = limit;
      this.state = checkNotNull(state, "state");
   }

   /**
    * 
    * @return URL to manipulate a specific realm
    */
   public URI getHref() {
      return href;
   }

   /**
    * 
    * @return A unique identifier for the realm
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * @return A short label
    */
   public String getName() {
      return name;
   }

   /**
    * for example limitation of how many machine you can launch in given region / how much computing
    * power is available for you.
    * 
    * @return Limits applicable for the current requester
    */
   @Nullable
   public String getLimit() {
      return limit;
   }

   /**
    * 
    * @return indicator of the realm's current state
    */
   public State getState() {
      return state;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((href == null) ? 0 : href.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((limit == null) ? 0 : limit.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((state == null) ? 0 : state.hashCode());
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
      Realm other = (Realm) obj;
      if (href == null) {
         if (other.href != null)
            return false;
      } else if (!href.equals(other.href))
         return false;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (limit == null) {
         if (other.limit != null)
            return false;
      } else if (!limit.equals(other.limit))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (state != other.state)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[href=" + href + ", id=" + id + ", limit=" + limit + ", name=" + name + ", state=" + state + "]";
   }

}
